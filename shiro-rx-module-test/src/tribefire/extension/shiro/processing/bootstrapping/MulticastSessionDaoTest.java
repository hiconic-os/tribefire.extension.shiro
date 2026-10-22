package tribefire.extension.shiro.processing.bootstrapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.Serializable;
import java.util.function.Function;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SimpleSession;
import org.junit.Test;

import com.braintribe.model.generic.eval.AbstractEvalContext;
import com.braintribe.model.generic.eval.EvalContext;
import com.braintribe.model.generic.eval.EvalException;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.model.service.api.MulticastRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.model.service.api.result.MulticastResponse;
import com.braintribe.model.service.api.result.ResponseEnvelope;
import com.braintribe.model.shiro.service.dist.GetSession;
import com.braintribe.model.shiro.service.dist.SerializedSession;
import com.braintribe.processing.async.api.AsyncCallback;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.oauth2.sdk.pkce.CodeVerifier;

public class MulticastSessionDaoTest {

    private static final String STATE_ATTRIBUTE = "pac4jRequestedStateEntraID";
    private static final String NONCE_ATTRIBUTE = "pac4jNonceEntraID";
    private static final String CODE_VERIFIER_ATTRIBUTE = "pac4jCodeVerifierEntraID";

    @Test
    public void transfersOidcSessionBetweenTwoInstances() {
        MulticastSessionDao source = dao("Reflex@node-a");
        MulticastSessionDao shiroDao = new MulticastSessionDao();
        MulticastSessionDao.INSTANCE = source;
        SimpleSession sourceSession = new SimpleSession();
        Serializable sessionId = shiroDao.create(sourceSession);

        State state = new State();
        String nonce = "nonce-value";
        CodeVerifier codeVerifier = new CodeVerifier();
        sourceSession.setAttribute(STATE_ATTRIBUTE, state);
        sourceSession.setAttribute(NONCE_ATTRIBUTE, nonce);
        sourceSession.setAttribute(CODE_VERIFIER_ATTRIBUTE, codeVerifier);
        shiroDao.update(sourceSession);

        MulticastSessionDao target = dao("Reflex@node-b");
        target.setRequestEvaluator(multicastEvaluator(source));
        MulticastSessionDao.INSTANCE = target;

        Session transferred = shiroDao.readSession(sessionId);

        assertThat(transferred).isNotSameAs(sourceSession);
        assertThat(transferred.getId()).isEqualTo(sessionId);
        assertThat(transferred.getAttribute(STATE_ATTRIBUTE)).isEqualTo(state);
        assertThat(transferred.getAttribute(NONCE_ATTRIBUTE)).isEqualTo(nonce);
        assertThat(((CodeVerifier) transferred.getAttribute(CODE_VERIFIER_ATTRIBUTE)).getValue()).isEqualTo(codeVerifier.getValue());
    }

    private static MulticastSessionDao dao(String instanceId) {
        MulticastSessionDao dao = new MulticastSessionDao();
        dao.setInstanceIdAsString(instanceId);
        return dao;
    }

    private static Evaluator<ServiceRequest> multicastEvaluator(MulticastSessionDao source) {
        return evaluator(request -> {
            MulticastRequest multicast = (MulticastRequest) request;
            GetSession getSession = (GetSession) multicast.getServiceRequest();

            SerializedSession serialized = SerializedSession.T.create();
            serialized.setShiroSessionId(getSession.getShiroSessionId());
            serialized.setSerializedSession(source.getLocalSessionSerialized(getSession.getShiroSessionId()));

            ResponseEnvelope envelope = ResponseEnvelope.T.create();
            envelope.setResult(serialized);

            SerializedSession localMiss = SerializedSession.T.create();
            localMiss.setShiroSessionId(getSession.getShiroSessionId());
            ResponseEnvelope localMissEnvelope = ResponseEnvelope.T.create();
            localMissEnvelope.setResult(localMiss);

            MulticastResponse response = MulticastResponse.T.create();
            response.getResponses().put(InstanceId.of("node-b", "Reflex"), localMissEnvelope);
            response.getResponses().put(InstanceId.of("node-a", "Reflex"), envelope);
            return response;
        });
    }

    private static Evaluator<ServiceRequest> evaluator(Function<ServiceRequest, Object> responder) {
        return new Evaluator<>() {
            @Override
            @SuppressWarnings("unchecked")
            public <T> EvalContext<T> eval(ServiceRequest request) {
                return result(() -> (T) responder.apply(request));
            }
        };
    }

    private static <T> EvalContext<T> result(ResultSupplier<T> supplier) {
        return new AbstractEvalContext<>() {
            @Override
            public T get() throws EvalException {
                return supplier.get();
            }

            @Override
            public void get(AsyncCallback<? super T> callback) {
                try {
                    callback.onSuccess(get());
                } catch (Throwable e) {
                    callback.onFailure(e);
                }
            }
        };
    }

    @FunctionalInterface
    private interface ResultSupplier<T> {
        T get();
    }
}

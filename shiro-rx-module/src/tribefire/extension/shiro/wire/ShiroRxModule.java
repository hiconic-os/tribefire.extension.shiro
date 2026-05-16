package tribefire.extension.shiro.wire;

import java.util.Arrays;
import java.util.List;

import com.braintribe.wire.api.module.WireModule;

import hiconic.rx.module.api.wire.RxModule;
import tribefire.extension.shiro.templates.wire.RxShiroTemplateWireModule;
import tribefire.extension.shiro.wire.space.ShiroRxModuleSpace;

public enum ShiroRxModule implements RxModule<ShiroRxModuleSpace> {

	INSTANCE;

	@Override
	public List<WireModule> dependencies() {
		return Arrays.asList(RxShiroTemplateWireModule.INSTANCE);
	}

}
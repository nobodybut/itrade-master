package com.trade.common.infrastructure.util.security;

import javax.script.*;

public class JavaScriptUtils {

	public static String script = "function js_encrypt(){var str='hq_'+new Date().getTime();var pwd='haoqiao';var prand=\"\";for(var i=0;i<pwd.length;i++){prand+=pwd.charCodeAt(i).toString()}var sPos=Math.floor(prand.length/5);var mult=parseInt(prand.charAt(sPos)+prand.charAt(sPos*2)+prand.charAt(sPos*3)+prand.charAt(sPos*4)+prand.charAt(sPos*5));var incr=Math.ceil(pwd.length/2);var modu=Math.pow(2,31)-1;if(mult<2){alert(\"Algorithm cannot find a suitable hash. Please choose a different password. \\nPossible considerations are to choose a more complex or longer password.\");return null}var salt=Math.round(Math.random()*1000000000)%100000000;prand+=salt;while(prand.length>10){prand=(parseInt(prand.substring(0,10))+parseInt(prand.substring(10,prand.length))).toString()}prand=(mult*prand+incr)%modu;var enc_chr=\"\";var enc_str=\"\";for(var i=0;i<str.length;i++){enc_chr=parseInt(str.charCodeAt(i)^Math.floor((prand/modu)*255));if(enc_chr<16){enc_str+=\"0\"+enc_chr.toString(16)}else enc_str+=enc_chr.toString(16);prand=(mult*prand+incr)%modu}salt=salt.toString(16);while(salt.length<8)salt=\"0\"+salt;enc_str+=salt;return enc_str}";
	public static ScriptEngineManager factory;
	public static ScriptEngine engine;
	public static Compilable compilingEngine;
	public static CompiledScript compiledScriptHaoqiao;

	static {
		try {
			factory = new ScriptEngineManager();
			engine = factory.getEngineByName("nashorn");
			compilingEngine = (Compilable) engine;
			compiledScriptHaoqiao = compilingEngine.compile(script);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public static String getHaoqiaoScriptValue() {
		String result = "";
		try {
			Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			compiledScriptHaoqiao.eval(bindings);
			Invocable invocable = (Invocable) compiledScriptHaoqiao.getEngine();
			result = invocable.invokeFunction("js_encrypt").toString();
		} catch (ScriptException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return result;
	}
}

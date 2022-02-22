import io.dialob.groovy.DialobDDRLFunction

class Test {
	
	@DialobDDRLFunction
	static String testFunction(String x) {
		return "blah " + x;
	}
}

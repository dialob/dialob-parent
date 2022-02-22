package scripts

import groovy.json.JsonSlurper
import io.dialob.groovy.DialobDDRLFunction

class FoaasService {
	
	@DialobDDRLFunction(async = true)
	static String foaas(String what, String who) {
		new JsonSlurper().parseText(
			"http://foaas.com/$what/$who/me".toURL().getText(requestProperties: ['Accept': 'application/json', 'User-Agent': 'Firefox'])
		).message
	}
}

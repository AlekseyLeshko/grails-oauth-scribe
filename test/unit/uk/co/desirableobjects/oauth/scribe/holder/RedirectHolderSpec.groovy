package uk.co.desirableobjects.oauth.scribe.holder

import grails.test.mixin.TestMixin
import spock.lang.Specification
import grails.test.mixin.support.GrailsUnitTestMixin
import org.springframework.mock.web.MockServletContext
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsWebRequest
import org.springframework.web.context.request.RequestContextHolder

/**
 * User: AlekseyLeshko
 * Date: 22/07/13
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */

@TestMixin(GrailsUnitTestMixin)
class RedirectHolderSpec extends Specification {
    def setup() {
        RequestContextHolder.metaClass.'static'.currentRequestAttributes = { ->
            new GrailsWebRequest(
                    new MockHttpServletRequest(),
                    new MockHttpServletResponse(),
                    new MockServletContext()
            )
        }
    }

    def 'RedirectHolder execute the method setUri(), set valid uri'() {
        def redirectUri = "http://test.com"

        when:
            RedirectHolder.setUri(redirectUri)
        then:
            def hash = RequestContextHolder.currentRequestAttributes()?.getSession()?.getAttribute(RedirectHolder.HASH_NAME)
            def uri = hash.get(RedirectHolder.URI_NAME)
            uri == redirectUri
    }

    def 'RedirectHolder execute the method setUri(), set valid uri with empty hash in session'() {
        def redirectUri = "http://test.com"

        given:
            def currentSession = RequestContextHolder.currentRequestAttributes()?.getSession()
            currentSession.putAt(RedirectHolder.HASH_NAME, [:])
        when:
            RedirectHolder.setUri(redirectUri)
        then:
            def hash = RequestContextHolder.currentRequestAttributes()?.getSession()?.getAttribute(RedirectHolder.HASH_NAME)
            def uri = hash.get(RedirectHolder.URI_NAME)
            uri == redirectUri
    }

    def 'RedirectHolder execute the method setUri(), set valid uri with custom hash in session'() {
        def redirectUri = "http://test.com"
        def testContent = "Test content"

        given:
            def currentSession = RequestContextHolder.currentRequestAttributes()?.getSession()
            currentSession.putAt(RedirectHolder.HASH_NAME, [testContent: testContent])
        when:
            RedirectHolder.setUri(redirectUri)
        then:
            def hash = RequestContextHolder.currentRequestAttributes()?.getSession()?.getAttribute(RedirectHolder.HASH_NAME)
            def uri = hash.get(RedirectHolder.URI_NAME)
            uri == redirectUri
    }

    def 'RedirectHolder execute the method setUri(), set invalid uri'() {
        def invalidRedirectUri = ""

        when:
            RedirectHolder.setUri(invalidRedirectUri)
        then:
            RequestContextHolder.currentRequestAttributes()?.getSession()?.getAttribute(RedirectHolder.HASH_NAME) == null
            RedirectHolder.getRedirect() == RedirectHolder.getDefaultRedirect()
    }

    def 'RedirectHolder execute the method getRedirect(), return valid hash'() {
        def redirectUri = "http://test.com"
        def hash = [:]
        hash.put(RedirectHolder.URI_NAME, redirectUri)

        given:
            RedirectHolder.setUri(redirectUri)
        when:
            def ex = RedirectHolder.getRedirect()
        then:
            ex == hash
    }

    def 'RedirectHolder execute the method getRedirect(), return redirect with custom uri'() {
        def redirectUri = "http://test.com"
        def hash = [:]
        hash.put(RedirectHolder.URI_NAME, redirectUri)

        given:
            RedirectHolder.setUri(redirectUri)
        when:
            def ex = RedirectHolder.getRedirect()
        then:
            ex == hash
    }

    def 'RedirectHolder execute the method getRedirect(), return default result'() {
        when:
            def ex = RedirectHolder.getRedirect()
        then:
         ex == RedirectHolder.getDefaultRedirect()
    }

    def 'RedirectHolder execute the method setRedirectHash(), set valid hash'() {
        def hash = [:]
        hash.put("controller", "object")
        hash.put("action", "show")
        hash.put("id", "1")

        when:
            RedirectHolder.setRedirectHash(hash)
        then:
            hash == RedirectHolder.getOrCreateRedirectHash()
    }

    def 'RedirectHolder execute the method setRedirectHash(), set invalid hash'() {
        when:
            RedirectHolder.setRedirectHash(null)
        then:
            RedirectHolder.getRedirect() == RedirectHolder.getDefaultRedirect()
    }

    def 'RedirectHolder execute the method getStorage(), return current session'() {
        when:
            def ex = RedirectHolder.getStorage()
        then:
            ex == RequestContextHolder.currentRequestAttributes()?.getSession()
    }

    def 'RedirectHolder execute the method getOrCreateRedirectHash(), return empty hash'() {
        when:
            def ex = RedirectHolder.getOrCreateRedirectHash()
        then:
            ex == [:]
    }

    def 'RedirectHolder execute the method getOrCreateRedirectHash(), return custom hash'() {
        def testContent = "Test content"
        def hash = [testContent: testContent]

        given:
            def currentSession = RequestContextHolder.currentRequestAttributes()?.getSession()
            currentSession.putAt(RedirectHolder.HASH_NAME, hash)
        when:
            def ex = RedirectHolder.getOrCreateRedirectHash()
        then:
            ex == hash
    }

    def 'RedirectHolder execute the method getDefaultRedirect(), return default redirect hash'() {
        given:
            def redirectHash = [:]
            redirectHash.put(RedirectHolder.URI_NAME, RedirectHolder.DEFAULT_URI)
        when:
            def ex = RedirectHolder.getDefaultRedirect()
        then:
            ex == redirectHash
    }
}

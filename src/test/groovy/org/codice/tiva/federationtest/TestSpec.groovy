import groovyx.net.http.AuthConfig
import groovyx.net.http.RESTClient
import spock.lang.Ignore
import spock.lang.Specification

class TestSpec extends Specification {

    def httpClient = new RESTClient('https://localhost:8993').with {
        it.authConfig = new AuthConfig(it).with {
            it.basic(System.getProperty('user_name', 'admin'),
                System.getProperty('user_pass', 'admin'))
            return it
        }

        return it
    }

    def before() {
        // class setup
    }

    def "just a fake test plz work thx"() {
        setup:
        def myList = [1, 2, 3]

        when:
        myList.add(4)

        then:
        myList.size() == 4
    }

    @Ignore
    def "just a fake failing test "() {
        setup:
        def myList = [1, 2, 3]

        when:
        myList.add(4)

        then:
        myList.size() == 5
    }
}
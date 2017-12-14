import groovy.json.JsonOutput
import groovyx.net.http.AuthConfig
import groovyx.net.http.RESTClient
import spock.lang.Specification
import spock.lang.Timeout

import static groovyx.net.http.ContentType.JSON

@Timeout(value = 60)
class TestSpec extends Specification {

    def httpClient

    def setup() {
        httpClient = new Client().build()
    }
    def cleanup() {
        httpClient?.shutdown()
    }

    // Tprintln(JsonOutput.prettyPrint(JsonOutput.toJson(res.data)))

    def "Ensure /search/catalog is accessible"() {
        when:
        def res = httpClient.get(path: '/search/catalog')

        then:
        res.status == 200
        res.contentType == 'text/html'
    }

    def "Ensure /Search redirects to /search/catalog"() {
        setup:
        httpClient = new Client(followRedirects: false).build()

        when:
        def res = httpClient.get(path: '/search')

        then:
        res.status == 302
        with(res.headers.Location) {
            it != null
            it.endsWith('/search/catalog')
        }
    }


    def "Sources exist and are available"() {
        when:
        def res = httpClient.get(path: '/services/catalog/sources')

        then:
        assert res.data.size() == 1, "There should be two sources"
        with(res.data[0]) {
            it.id == 'ddf.distribution'
            it.available
        }

    }

    def "Query for arizona returns results"() {
        setup:
        def query = [src: 'ddf.distribution',
                    start: 1,
                    count: 250,
                    cql: 'anyText ILIKE \'arizona\'',
                    sort: 'modified:descending',
                    id: UUID.randomUUID().toString()]

        when:
        def res = httpClient.post(path: '/search/catalog/internal/cql',
                body: query,
                requestContentType: JSON)

        then:
        res.status == 200
        res.contentType =~ $/(application|text)/json/$
        res.data.status.hits == 752
    }

    /****************************************
     * SUPPORT UTILITIES
     ***************************************/

    static class Client {
        String path = 'https://newui.phx.connexta.com:8993'
        boolean followRedirects = true

        def build() {
            new RESTClient(path).with {
                it.authConfig = new AuthConfig(it).with {
                    it.basic(System.getProperty('user_name', 'admin'),
                            System.getProperty('user_pass', 'admin'))
                    return it
                }

                it.ignoreSSLIssues()
                it.client.params
                        .setBooleanParameter('http.protocol.handle-redirects', followRedirects)

                return it
            }
        }
    }

}
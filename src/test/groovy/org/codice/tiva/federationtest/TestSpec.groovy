import spock.lang.Ignore
import spock.lang.Specification

class TestSpec extends Specification {

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
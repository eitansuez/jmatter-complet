/**
 * Created by IntelliJ IDEA.
 * User: Eitan Suez
 * Date: Sep 14, 2008
 * Time: 10:32:29 AM
 */


println "3" * 5
name = "eitan"
println "hello ${name}"


def mylist = ["one", "two", "three"];
mylist.each { String item ->
   println item
}
def result = mylist.collect { String item -> "my ${item}" }
println result.join(",")


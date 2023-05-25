package simulations;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;

public class UsersSimulations extends Simulation {

    //headers and url setup
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://fakerestapi.azurewebsites.net")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");


    private static ChainBuilder createNewUser =
            exec(http("Create new user")
                    .post("/api/v1/Users")
                    .body(StringBody(
                            "{\n" +
                                    "    \"id\": 10,\n" +
                                    "    \"userName\": \"John\",\n" +
                                    "    \"password\": \"Smith\"\n" +
                                    "}"
                    )));

    private static ChainBuilder getAllUsers =
            exec(http("Get all users")
                    .get("/api/v1/Users"));


    // Scenario definition
    private ScenarioBuilder scn = scenario("Users - full simulation")
            .forever().on(
                    exec(getAllUsers)
                            .pause(2)
                            .exec(createNewUser)
            );


    // Load simulation
    {
        setUp(
                scn.injectOpen(
                        nothingFor(3),
                        rampUsers(5).during(10),
                        constantUsersPerSec(10).during(10)
                ).protocols(httpProtocol)
        ).maxDuration(30);
    }


}

import Entities.Agencia;
import Entities.Compras;
import Entities.Usuario;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.filter.log.ErrorLoggingFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.module.jsv.JsonSchemaValidator.*;
import static org.hamcrest.Matchers.*;

public class AgenciaTests {
    public static Faker faker;
    private static RequestSpecification request;
    private static Agencia agencia;
    private static Compras compras;
    private static Usuario usuario;

    @BeforeAll
    public static void Setup(){
        RestAssured.baseURI = "https://restful-agencydiogft.herokuapp.com";
        faker = new Faker();
        usuario = new Usuario(faker.id().usuarioId(123),
                faker.name().toString(),
                faker.email().toString(),
                faker.senha().toString(1234),
                faker.permissoes().toString());

        agencia = new Agencia(usuario.getFullName(),
                (float)faker.number().randomDouble(),
                true,agencia
                "");
        compras = new Compras("12000", "6000");
                (float)faker.number().randomDouble(12000, 6000),
                true,compras,
                "");
        RestAssured.filters(new RequestLoggingFilter(),new ResponseLoggingFilter(), new ErrorLoggingFilter());
    }

    @BeforeEach
    void setRequest(){
        request = given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                .contentType(ContentType.JSON)
                .auth().basic("admin", "password123");
    }

    @Test
    public void getAllAgenciaById_returnOk(){
            Response response = request
                                    .when()
                                        .get("/agencia")
                                    .then()
                                        .extract()
                                        .response();


        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void  getAllAgenciaByUserFirstName_AgenciaExists_returnOk(){
                    request
                        .when()
                            .queryParam("firstName", "AnaMaria")
                            .get("/agencia")
                        .then()
                            .assertThat()
                            .statusCode(200)
                            .contentType(ContentType.JSON)
                        .and()
                        .body("results", hasSize(greaterThan(0)));

    }

    @Test
    public void  CreateAgencia_WithValidData_returnOk(){

        Booking test = agencia;
        given().config(RestAssured.config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails()))
                    .contentType(ContentType.JSON)
                        .when()
                        .body(agencia)
                        .post("/agencia")
                        .then()
                        .body(matchesJsonSchemaInClasspath("createAgenciaRequestSchema.json"))
                        .and()
                        .assertThat()
                        .statusCode(200)
                        .contentType(ContentType.JSON).and().time(lessThan(2000L));



    }

}

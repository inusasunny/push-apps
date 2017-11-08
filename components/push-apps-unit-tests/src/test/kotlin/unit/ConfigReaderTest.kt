package unit

import io.pivotal.pushapps.ConfigReader
import io.pivotal.pushapps.DatabaseDriver
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it

class ConfigReaderTest : Spek({
    describe("#parseConfig") {
        it("Parses the pushAppsConfig config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")
            assertThat(pushAppsConfig.pushApps.appDeployRetryCount).isEqualTo(3)
        }

        it("Parses the cf config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")

            assertThat(pushAppsConfig.cf.apiHost).isEqualTo("api.example.com")
            assertThat(pushAppsConfig.cf.username).isEqualTo("some-username")
            assertThat(pushAppsConfig.cf.password).isEqualTo("some-password")
            assertThat(pushAppsConfig.cf.organization).isEqualTo("some-organization")
            assertThat(pushAppsConfig.cf.space).isEqualTo("some-space")
            assertThat(pushAppsConfig.cf.skipSslValidation).isTrue()
            assertThat(pushAppsConfig.cf.dialTimeoutInMillis).isEqualTo(1000)
        }

        it("Parses the apps config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")

            val apps = pushAppsConfig.apps
            assertThat(apps).hasSize(1)

            val app1 = apps[0]
            assertThat(app1.name).isEqualTo("some-name")
            assertThat(app1.path).isEqualTo("some-path")
            assertThat(app1.buildpack).isEqualTo("some-buildpack")

            assertThat(app1.environment).isEqualTo(mapOf("FRUIT" to "lemons"))
            assertThat(app1.serviceNames).isEqualTo(listOf("some-service-name"))

            assertThat(app1.route!!.hostname).isEqualTo("lemons")
            assertThat(app1.route!!.path).isEqualTo("/citrus")
        }

        it("Parses the services config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")

            val services = pushAppsConfig.services
            assertThat(services).hasSize(1)

            val service = services!![0]
            assertThat(service.name).isEqualTo("some-service-name")
            assertThat(service.plan).isEqualTo("a-good-one")
            assertThat(service.broker).isEqualTo("some-broker")
            assertThat(service.optional).isTrue()
        }

        it("Parses the security group config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")

            val securityGroups = pushAppsConfig.securityGroups
            assertThat(securityGroups).hasSize(1)

            val securityGroup = securityGroups!![0]
            assertThat(securityGroup.name).isEqualTo("some-group")
            assertThat(securityGroup.destination).isEqualTo("some-destination")
            assertThat(securityGroup.protocol).isEqualTo("all")
        }

        it("Parses the user provided services config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")

            val userProvidedServices = pushAppsConfig.userProvidedServices
            assertThat(userProvidedServices).hasSize(1)

            val service = userProvidedServices!![0]
            assertThat(service.name).isEqualTo("some-user-provided-service-name")
            assertThat(service.credentials).isEqualTo(mapOf("username" to "some-username"))
        }

        it("Parses the db migration config") {
            val pushAppsConfig = ConfigReader.parseConfig("src/test/kotlin/support/exampleConfig.yml")

            val migrations = pushAppsConfig.migrations
            assertThat(migrations).hasSize(1)

            val migration = migrations!![0]
            assertThat(migration.user).isEqualTo("user")
            assertThat(migration.password).isEqualTo("password")
            assertThat(migration.driver).isInstanceOfAny(DatabaseDriver.Postgres::class.java)
            assertThat(migration.host).isEqualTo("10.0.0.1")
            assertThat(migration.port).isEqualTo("5432")
            assertThat(migration.schema).isEqualTo("metrics")
            assertThat(migration.migrationDir).isEqualTo("/all/the/cool/migrations")
        }
    }
})
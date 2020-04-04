package org.example

import helpers.Helper

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.util.Random

class SimulationForAzure extends Simulation {

	val httpProtocol = http
		.baseUrl("http://computer-database.gatling.io")
		.inferHtmlResources()
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en;q=0.5")
		.upgradeInsecureRequestsHeader("1")
		.userAgentHeader("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:73.0) Gecko/20100101 Firefox/73.0")

	val headers = Map("Origin" -> "http://computer-database.gatling.io")

	val scn = scenario("SimulationForAzure")
		// Prepare data
  	.exec(
			session => session
				.set("randomName", Helper.getRandomName())
  			.set("randomCompamy", new Random().nextInt(12)+1)
		)
		// Go to main page
		.exec(http("Go to main page")
			.get("/"))
		.pause(3,5)
		// Click any comp
		.exec(http("Click any comp")
			.get("/computers/355"))
		.pause(3,5)
		// Click cancel
		.exec(http("Click cancel")
			.get("/computers"))
		.pause(7,9)
		// Enter name and click filter
		.exec(http("Enter name and click filter")
			.get("/computers?f=Mac"))
		.pause(8,10)
		// Click add new
		.exec(http("Click add new")
			.get("/computers/new"))
		.pause(13,16)
		// Input data and click create
		.exec(http("Input data and click create")
			.post("/computers")
			.headers(headers)
			.formParam("name", "${randomName}")
			.formParam("introduced", "2020-04-04")
			.formParam("discontinued", "2021-04-03")
			.formParam("company", "${randomCompamy}"))
		.pause(14,17)
		// Enter name of created comp and click filter
		.exec(http("Enter name of created comp and click filter")
			.get("/computers?f=${randomName}"))

	setUp(scn.inject(atOnceUsers(2))).protocols(httpProtocol)
}
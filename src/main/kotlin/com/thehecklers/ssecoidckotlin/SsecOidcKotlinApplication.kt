package com.thehecklers.ssecoidckotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@SpringBootApplication
class SsecOidcKotlinApplication {
	@Bean
	fun client(regRepo: ClientRegistrationRepository, cliRepo: OAuth2AuthorizedClientRepository): WebClient {
		val filter = ServletOAuth2AuthorizedClientExchangeFilterFunction(regRepo, cliRepo)

		filter.setDefaultOAuth2AuthorizedClient(true)

		return WebClient.builder()
			.baseUrl("http://localhost:8081/resources")
			.apply(filter.oauth2Configuration())
			.build()
	}
}

fun main(args: Array<String>) {
	runApplication<SsecOidcKotlinApplication>(*args)
}

@RestController
class OidcController(private val client: WebClient) {
	@GetMapping("/")
	fun hello() = "Hello KotlinConf!"

	@GetMapping("/something")
	fun getSomething() = client.get()
		.uri("/something")
		.retrieve()
		.bodyToMono<String>()
		.block();

	@GetMapping("/claims")
	fun getClaims() = client.get()
		.uri("/claims")
		.retrieve()
		.bodyToMono<Map<String, Any>>()
		.block()

	@GetMapping("/email")
	fun getSubject() = client.get()
		.uri("/email")
		.retrieve()
		.bodyToMono<String>()
		.block();
}
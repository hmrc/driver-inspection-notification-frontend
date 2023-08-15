/*
 * Copyright 2023 HM Revenue & Customs
 *
 */

package uk.gov.hmrc.driverinspectionnotificationfrontend.helpers

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.client.{MappingBuilder, ResponseDefinitionBuilder}
import com.github.tomakehurst.wiremock.stubbing.StubMapping

trait WireMockHelper {
  this: BaseISpec =>

  def stub(method: MappingBuilder, response: ResponseDefinitionBuilder): StubMapping =
    stubFor(method.willReturn(response))

  def stubGet(uri: String, responseBody: String): StubMapping =
    stub(get(urlEqualTo(uri)), okJson(responseBody))

  def stubGetNotFound(uri: String): StubMapping =
    stub(get(urlEqualTo(uri)), notFound)

  def stubPost(url: String, status: Integer, responseBody: String): StubMapping = {
    removeStub(post(urlMatching(url)))
    stubFor(
      post(urlMatching(url))
        .willReturn(
          aResponse().withStatus(status).withBody(responseBody)
        ))
  }

  def stubPut(url: String, responseStatus: Integer, responseBody: String): StubMapping = {
    removeStub(put(urlMatching(url)))
    stubFor(
      put(urlMatching(url))
        .willReturn(
          aResponse().withStatus(responseStatus).withBody(responseBody)
        ))
  }
}

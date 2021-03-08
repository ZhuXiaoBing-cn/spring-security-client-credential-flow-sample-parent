// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.zhu.springsecurityclientcredentialflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Controller
public class WebController {

    private String URI_READ_ENDPOINT = "http://localhost:8081/read";

    private String URI_WRITE_ENDPOINT = "http://localhost:8081/write";

    @Autowired
    private WebClient webClient;

    @GetMapping("/client_file_read")
    @ResponseBody
    public String fileRead() {
        String body = webClient
            .get()
            .uri(URI_READ_ENDPOINT)
            .attributes(clientRegistrationId("aad-example"))
            .retrieve()
            .bodyToMono(String.class)
            .block();
        return "response:" + (null != body ? body : "failed.");

    }

    @GetMapping("/client_file_write")
    @ResponseBody
    public String fileWrite(@RegisteredOAuth2AuthorizedClient("aad-example") OAuth2AuthorizedClient authorizedClient) {
        return callCustomResourceServer(authorizedClient);
    }


    private String callCustomResourceServer(OAuth2AuthorizedClient authorizedClient) {
        if (null != authorizedClient) {
            String body = webClient
                .get()
                .uri(URI_WRITE_ENDPOINT)
                .attributes(oauth2AuthorizedClient(authorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();
            return "response:" + (null != body ? body : "failed.");
        } else {
            return "response failed.";
        }
    }


}

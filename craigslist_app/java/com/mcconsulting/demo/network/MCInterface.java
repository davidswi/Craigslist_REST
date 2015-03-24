package com.mcconsulting.demo.network;

import com.mcconsulting.demo.model.Contact;
import com.mcconsulting.demo.model.Address;
import com.mcconsulting.demo.model.PhoneNumber;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Retrofit interface for demo API.
 */
public interface MCInterface {
    @POST("/oauth2/token")
    Observable<OAuthResponse> getOAuthToken(@Body OAuthRequest oAuthRequest);

    @GET("/api/contacts")
    Observable<List<Contact>> getContacts();
}

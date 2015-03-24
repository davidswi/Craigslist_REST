package com.mcconsulting.demo.network;

import com.mcconsulting.demo.model.Contact;
import com.mcconsulting.demo.model.Address;
import com.mcconsulting.demo.model.PhoneNumber;

import java.util.List;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * API class.  Calls are async, to allow calling from the main thread without having to create an AsyncTask.
 * Responses are passed in a callback which is guaranteed to be called on the main thread allowing immediate UI update.
 */

public class MCService {
    public static interface LoginCallback {
        void onLoginComplete (OAuthResponse oAuthResponse);
        void onLoginError (String error);
    }

    public static interface GeContactsCallback {
        void onGetContacts (List<Contact> contacts);
        void onGetContactsError (String error);
    }

    private final static String OAUTH_GRANT_TYPE = "bearer";
    private final static String OAUTH_CLIENT_ID = "1";

    private String mAccessToken = null;
    private MetacraftInterface mMetacraftInterface;

    public String getAccessToken () {
        return mAccessToken;
    }

    public void getOAuthToken (String username, String password, final LoginCallback callback) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(buildEndpoint())
                .setRequestInterceptor(new DefaultRequestInterceptor())
                .setErrorHandler(new DefaultErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        MCInterface mcInterface = restAdapter.create(MCInterface.class);

        mcInterface.getOAuthToken(new OAuthRequest(username, password, OAUTH_GRANT_TYPE, OAUTH_CLIENT_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<OAuthResponse>() {
                    @Override
                    public void call(OAuthResponse oAuthResponse) {
                        mAccessToken = oAuthResponse.access_token;
                        callback.onLoginComplete(oAuthResponse);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onLoginError(getErrorMessage(throwable));
                    }
                });
    }

    public void getContacts (final GetContactsCallback callback) {
        if (!buildService()) {
            callback.onGetContactsError("No AccessToken");
        }

        mMCInterface.getContacts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Contact>>() {
                    @Override
                    public void call(List<Contact> contacts) {
                        callback.onGetContacts(contacts);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        callback.onGetBeersError(getErrorMessage(throwable));
                    }
                });
    }

    private String buildEndpoint () {
        // TODO Read from settings
        return "https://demo.mcconsulting.com;
    }

    private String getErrorMessage (Throwable throwable) {
        String errorMessage = null;
        if (throwable instanceof RetrofitError) {
            RetrofitError retrofitError = (RetrofitError) throwable;
            Response response = retrofitError.getResponse();

            if (null != response) {
                errorMessage = response.getReason();
            }
        }

        if (null == errorMessage) {
            errorMessage = throwable.getMessage();
        }
        return errorMessage;
    }

    private boolean buildService () {
        if (null == mAccessToken) return false;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(buildEndpoint())
                .setRequestInterceptor(new OAuthRequestInterceptor(mAccessToken))
                .setErrorHandler(new DefaultErrorHandler())
                .setLogLevel(RestAdapter.LogLevel.FULL)     // TODO: Remove from debug build.
                .build();

        mMCInterface = restAdapter.create(MCInterface.class);
        return (null != mMCInterface);
    }

    // TODO Determine if there is anything to do here.  Currently just for debugging.
    private static class DefaultErrorHandler implements ErrorHandler {
        @Override
        public Throwable handleError(RetrofitError cause) {
            Response r = cause.getResponse();
            String reason;

            if (r != null) {
                r.getReason();
            }

            return cause;
        }
    }
}

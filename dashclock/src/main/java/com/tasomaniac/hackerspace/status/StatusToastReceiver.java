package com.tasomaniac.hackerspace.status;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.tasomaniac.hackerspace.status.data.HackerSpacePreference;
import com.tasomaniac.hackerspace.status.data.model.SpaceApiResponse;

import javax.inject.Inject;

import retrofit.Callback;
import retrofit.Response;

public class StatusToastReceiver extends BroadcastReceiver {

    @Inject
    HackerSpacePreference hackerSpacePreference;
    @Inject
    SpaceApiService spaceApiService;

    @Override
    public void onReceive(final Context context, Intent intent) {
        App.get(context).component().inject(this);

        final String url = hackerSpacePreference.getHackerSpace().url;
        if (TextUtils.isEmpty(url)) {
            return;
        }

        spaceApiService.spaceStatus(url).enqueue(new Callback<SpaceApiResponse>() {
            @Override
            public void onResponse(Response<SpaceApiResponse> response) {
                if (response.isSuccess()) {
                    final SpaceApiResponse body = response.body();
                    final String name = body.getSpace();
                    final Boolean open = body.getState().isOpen();
                    final String status = context.getString(open == null
                            ? R.string.unknown : (open ? R.string.open : R.string.closed));
                    Toast.makeText(context.getApplicationContext(),
                            (name != null ? name : "Your hackerspace") + " is " + status,
                            Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }
}

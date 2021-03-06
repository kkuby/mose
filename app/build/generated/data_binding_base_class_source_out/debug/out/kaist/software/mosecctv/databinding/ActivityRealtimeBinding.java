// Generated by view binder compiler. Do not edit!
package kaist.software.mosecctv.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import kaist.software.mosecctv.R;

public final class ActivityRealtimeBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button audio;

  @NonNull
  public final TextView connectionState;

  @NonNull
  public final Button notification;

  @NonNull
  public final Button update;

  private ActivityRealtimeBinding(@NonNull ConstraintLayout rootView, @NonNull Button audio,
      @NonNull TextView connectionState, @NonNull Button notification, @NonNull Button update) {
    this.rootView = rootView;
    this.audio = audio;
    this.connectionState = connectionState;
    this.notification = notification;
    this.update = update;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRealtimeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRealtimeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_realtime, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRealtimeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.audio;
      Button audio = rootView.findViewById(id);
      if (audio == null) {
        break missingId;
      }

      id = R.id.connectionState;
      TextView connectionState = rootView.findViewById(id);
      if (connectionState == null) {
        break missingId;
      }

      id = R.id.notification;
      Button notification = rootView.findViewById(id);
      if (notification == null) {
        break missingId;
      }

      id = R.id.update;
      Button update = rootView.findViewById(id);
      if (update == null) {
        break missingId;
      }

      return new ActivityRealtimeBinding((ConstraintLayout) rootView, audio, connectionState,
          notification, update);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

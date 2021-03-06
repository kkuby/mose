// Generated by view binder compiler. Do not edit!
package kaist.software.mosecctv.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import kaist.software.mosecctv.R;

public final class DialogLogViewBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView comment;

  @NonNull
  public final TextView dt;

  @NonNull
  public final ImageView imageView;

  @NonNull
  public final ProgressBar progressBar;

  @NonNull
  public final TextView title;

  @NonNull
  public final VideoView videoView;

  @NonNull
  public final TextView visitor;

  private DialogLogViewBinding(@NonNull ConstraintLayout rootView, @NonNull TextView comment,
      @NonNull TextView dt, @NonNull ImageView imageView, @NonNull ProgressBar progressBar,
      @NonNull TextView title, @NonNull VideoView videoView, @NonNull TextView visitor) {
    this.rootView = rootView;
    this.comment = comment;
    this.dt = dt;
    this.imageView = imageView;
    this.progressBar = progressBar;
    this.title = title;
    this.videoView = videoView;
    this.visitor = visitor;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DialogLogViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DialogLogViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dialog_log_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DialogLogViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.comment;
      TextView comment = rootView.findViewById(id);
      if (comment == null) {
        break missingId;
      }

      id = R.id.dt;
      TextView dt = rootView.findViewById(id);
      if (dt == null) {
        break missingId;
      }

      id = R.id.imageView;
      ImageView imageView = rootView.findViewById(id);
      if (imageView == null) {
        break missingId;
      }

      id = R.id.progress_bar;
      ProgressBar progressBar = rootView.findViewById(id);
      if (progressBar == null) {
        break missingId;
      }

      id = R.id.title;
      TextView title = rootView.findViewById(id);
      if (title == null) {
        break missingId;
      }

      id = R.id.videoView;
      VideoView videoView = rootView.findViewById(id);
      if (videoView == null) {
        break missingId;
      }

      id = R.id.visitor;
      TextView visitor = rootView.findViewById(id);
      if (visitor == null) {
        break missingId;
      }

      return new DialogLogViewBinding((ConstraintLayout) rootView, comment, dt, imageView,
          progressBar, title, videoView, visitor);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

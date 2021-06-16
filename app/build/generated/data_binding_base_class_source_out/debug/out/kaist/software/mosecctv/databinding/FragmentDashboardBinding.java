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
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import kaist.software.mosecctv.R;

public final class FragmentDashboardBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button addVisitor;

  @NonNull
  public final TextView txt1;

  @NonNull
  public final RecyclerView visitorListView;

  private FragmentDashboardBinding(@NonNull ConstraintLayout rootView, @NonNull Button addVisitor,
      @NonNull TextView txt1, @NonNull RecyclerView visitorListView) {
    this.rootView = rootView;
    this.addVisitor = addVisitor;
    this.txt1 = txt1;
    this.visitorListView = visitorListView;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentDashboardBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentDashboardBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_dashboard, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentDashboardBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addVisitor;
      Button addVisitor = rootView.findViewById(id);
      if (addVisitor == null) {
        break missingId;
      }

      id = R.id.txt1;
      TextView txt1 = rootView.findViewById(id);
      if (txt1 == null) {
        break missingId;
      }

      id = R.id.visitor_list_view;
      RecyclerView visitorListView = rootView.findViewById(id);
      if (visitorListView == null) {
        break missingId;
      }

      return new FragmentDashboardBinding((ConstraintLayout) rootView, addVisitor, txt1,
          visitorListView);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

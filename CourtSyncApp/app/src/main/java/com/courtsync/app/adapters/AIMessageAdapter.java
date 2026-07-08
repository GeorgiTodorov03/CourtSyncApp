package com.courtsync.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.courtsync.app.R;
import com.courtsync.app.models.AIMessage;
import java.util.ArrayList;
import java.util.List;

public class AIMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 0;
    private static final int VIEW_TYPE_ASSISTANT = 1;

    private List<AIMessage> messages = new ArrayList<>();

    public void submitList(List<AIMessage> newMessages) {
        messages = new ArrayList<>(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(AIMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_ASSISTANT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_ai_message_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_ai_message_assistant, parent, false);
            return new AssistantViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AIMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).bind(message);
        } else {
            ((AssistantViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;

        UserViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }

        void bind(AIMessage msg) {
            tvMessage.setText(msg.getContent());
            tvTimestamp.setText(msg.getTimestamp());
        }
    }

    static class AssistantViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        View hallSuggestion;

        AssistantViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            hallSuggestion = itemView.findViewById(R.id.hallSuggestion);
        }

        void bind(AIMessage msg) {
            tvMessage.setText(msg.getContent());
            tvTimestamp.setText(msg.getTimestamp());
            if (msg.getSuggestedHalls() != null && !msg.getSuggestedHalls().isEmpty()) {
                hallSuggestion.setVisibility(View.VISIBLE);
                TextView tvName = hallSuggestion.findViewById(R.id.tvSuggestionName);
                TextView tvAvail = hallSuggestion.findViewById(R.id.tvSuggestionAvailability);
                tvName.setText(msg.getSuggestedHalls().get(0).getName());
                tvAvail.setText("Available now");
            } else {
                hallSuggestion.setVisibility(View.GONE);
            }
        }
    }
}

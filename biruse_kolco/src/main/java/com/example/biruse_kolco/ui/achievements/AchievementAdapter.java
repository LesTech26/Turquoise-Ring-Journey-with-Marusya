package com.example.biruse_kolco.ui.achievements;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.biruse_kolco.R;
import com.example.biruse_kolco.data.database.entities.Achievement;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.ViewHolder> {

    private List<Achievement> achievements = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.bind(achievement);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public void setAchievements(List<Achievement> achievements) {
        this.achievements = achievements;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvIcon;
        private final TextView tvTitle;
        private final TextView tvDesc;
        private final TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIcon = itemView.findViewById(R.id.tv_achievement_icon);
            tvTitle = itemView.findViewById(R.id.tv_achievement_title);
            tvDesc = itemView.findViewById(R.id.tv_achievement_desc);
            tvStatus = itemView.findViewById(R.id.tv_achievement_status);
        }

        void bind(Achievement achievement) {
            tvIcon.setText(achievement.getIcon());
            tvTitle.setText(achievement.getTitle());
            tvDesc.setText(achievement.getDescription());

            if (achievement.isUnlocked()) {
                tvStatus.setText("✅ Открыто");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.gold));
                itemView.setAlpha(1.0f);
            } else {
                tvStatus.setText("🔒 " + achievement.getRequirement() + " районов");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.orehovy));
                itemView.setAlpha(0.6f);
            }
        }
    }
}
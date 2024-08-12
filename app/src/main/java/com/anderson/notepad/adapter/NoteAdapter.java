package com.anderson.notepad.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.anderson.notepad.R;
import com.anderson.notepad.database.table.Note;
import com.anderson.notepad.databinding.ViewHolderNoteBinding;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteViewHolder> {

    private LayoutInflater inflater;

    private OnItemClickListener onItemClickListener;
    private OnUpdateSelectedListListener onUpdateSelectedListListener;

    private boolean inSelectionMode;

    private List<Note> selectedNotes = new ArrayList<>();

    public NoteAdapter() {
        super(new DiffUtil.ItemCallback<Note>() {

            @Override
            public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
                return oldItem.getTitle().equals(newItem.getTitle()) &&
                       oldItem.getLastEditionTimestamp().equals(newItem.getLastEditionTimestamp());
            }

        });
    }


    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        ViewHolderNoteBinding binding = DataBindingUtil.inflate(inflater, R.layout.view_holder_note, parent, false);
        binding.setContext(parent.getContext());
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull NoteViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.binding.setNote(null);
    }

    public Note getNoteAt(int position) {
        return getItem(position);
    }

    public List<Note> getSelectedNotes() {
        return selectedNotes;
    }

    public boolean isEveryNoteSelected() {
        return selectedNotes.size() == getCurrentList().size();
    }

    public void exitSelectionMode() {
        deselectAll();
        inSelectionMode = false;
    }

    public boolean isInSelectionMode() {
        return inSelectionMode;
    }

    public void selectAll() {

        if (inSelectionMode) {

            selectedNotes.clear();
            selectedNotes.addAll(getCurrentList());

            for (int i = 0; i < selectedNotes.size(); i++) {
                notifyItemChanged(i);
            }

        }
    }

    public void deselectAll() {
        if (inSelectionMode) {

            List<Note> allNotes = getCurrentList();

            for (int i = 0; i < allNotes.size(); i++) {
                Note item = allNotes.get(i);

                if (selectedNotes.contains(item)) {
                    selectedNotes.remove(item);
                    notifyItemChanged(i);
                }

            }

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnUpdateSelectedListListener(OnUpdateSelectedListListener onUpdateSelectedListListener) {
        this.onUpdateSelectedListListener = onUpdateSelectedListListener;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {

        private ViewHolderNoteBinding binding;

        private ColorStateList defaultCardBackgroundColor;

        public NoteViewHolder(@NonNull ViewHolderNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Note note) {
            binding.setNote(note);
            toggleSelectedCardColor(note);

            itemView.setOnClickListener(view -> {

                if (!inSelectionMode && onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, NoteAdapter.this, getAdapterPosition());
                }

                if (inSelectionMode) {

                    addOrRemoveFromSelectedNotes(note);

                    if (onUpdateSelectedListListener != null) {
                        onUpdateSelectedListListener.onUpdateSelectedList(NoteAdapter.this, selectedNotes);
                    }

                }

            });

            itemView.setOnLongClickListener(view -> {

                if (!inSelectionMode) {

                    inSelectionMode = true;

                    addOrRemoveFromSelectedNotes(note);

                    if (onUpdateSelectedListListener != null) {
                        onUpdateSelectedListListener.onUpdateSelectedList(NoteAdapter.this, selectedNotes);
                    }

                }

                return true;

            });

        }

        private void addOrRemoveFromSelectedNotes(Note note) {
            if (inSelectionMode && !selectedNotes.contains(note)) {
                selectedNotes.add(note);
                selected();
            } else if (inSelectionMode) {
                selectedNotes.remove(note);
                deselected();
            }
        }

        private void toggleSelectedCardColor(Note note) {
            if (inSelectionMode && selectedNotes.contains(note)) {
                selected();
            } else if (inSelectionMode && !selectedNotes.contains(note) || !inSelectionMode) {
                deselected();
            }
        }

        private void selected() {
            defaultCardBackgroundColor = binding.card.getCardBackgroundColor();
            binding.card.setCardBackgroundColor(Color.LTGRAY);
        }

        private void deselected() {
            if (defaultCardBackgroundColor != null) {
                binding.card.setCardBackgroundColor(defaultCardBackgroundColor);
            }
        }

    }

    public interface OnItemClickListener {
        void onItemClick(View view, NoteAdapter noteAdapter, int position);
    }

    public interface OnUpdateSelectedListListener {
        void onUpdateSelectedList(NoteAdapter noteAdapter, List<Note> selecteds);
    }

}

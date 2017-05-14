package com.pchmn.materialchips.adapter;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pchmn.materialchips.ChipsInput;
import com.pchmn.materialchips.R;
import com.pchmn.materialchips.model.ChipInterface;
import com.pchmn.materialchips.util.ColorUtil;
import com.pchmn.materialchips.util.LetterTileProvider;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class FilterableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    // list
    private final List<ChipInterface> mOriginalList = new ArrayList<>();
    private final List<ChipInterface> mChipList = new ArrayList<>();
    private final List<ChipInterface> mFilteredList = new ArrayList<>();
    private ChipFilter mFilter;
    private final ChipsInput mChipsInput;
    private final LetterTileProvider mLetterTileProvider;
    private final ColorStateList mBackgroundColor;
    private final ColorStateList mTextColor;
    // recycler
    private final RecyclerView mRecyclerView;


    public FilterableAdapter(Context context,
                             RecyclerView recyclerView,
                             List<? extends ChipInterface> chipList,
                             ChipsInput chipsInput,
                             ColorStateList backgroundColor,
                             ColorStateList textColor) {
        mRecyclerView = recyclerView;
        Collections.sort(chipList, (Comparator<ChipInterface>) (o1, o2) -> {
            Collator collator = Collator.getInstance(Locale.getDefault());
            collator.setStrength(Collator.PRIMARY);
            return collator.compare(o1.getLabel(), o2.getLabel());
        });
        mOriginalList.addAll(chipList);
        mChipList.addAll(chipList);
        mFilteredList.addAll(chipList);
        mLetterTileProvider = new LetterTileProvider(context);
        mBackgroundColor = backgroundColor;
        mTextColor = textColor;
        mChipsInput = chipsInput;

        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                removeChip(chip);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                addChip(chip);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                mRecyclerView.scrollToPosition(0);
            }
        });
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView mAvatar;
        private final TextView mLabel;
        private final TextView mInfo;

        ItemViewHolder(View view) {
            super(view);
            mAvatar = (CircleImageView) view.findViewById(R.id.avatar);
            mLabel = (TextView) view.findViewById(R.id.label);
            mInfo = (TextView) view.findViewById(R.id.info);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_filterable, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        final ChipInterface chip = getItem(position);

        // avatar
        if(mChipsInput.chipHasAvatarIcon() && chip.getAvatarUri() != null) {
            itemViewHolder.mAvatar.setVisibility(View.VISIBLE);
            itemViewHolder.mAvatar.setImageURI(chip.getAvatarUri());
        }
        else if(mChipsInput.chipHasAvatarIcon() && chip.getAvatarDrawable() != null) {
            itemViewHolder.mAvatar.setVisibility(View.VISIBLE);
            itemViewHolder.mAvatar.setImageDrawable(chip.getAvatarDrawable());
        }
        else if(mChipsInput.chipHasAvatarIcon()) {
            itemViewHolder.mAvatar.setVisibility(View.VISIBLE);
            itemViewHolder.mAvatar.setImageBitmap(mLetterTileProvider.getLetterTile(chip.getLabel()));
        }
        else {
            itemViewHolder.mAvatar.setVisibility(GONE);
        }

        // label
        itemViewHolder.mLabel.setText(chip.getLabel());

        // info
        if(chip.getInfo() != null) {
            itemViewHolder.mInfo.setVisibility(View.VISIBLE);
            itemViewHolder.mInfo.setText(chip.getInfo());
        }
        else {
            itemViewHolder.mInfo.setVisibility(GONE);
        }

        // colors
        if(mBackgroundColor != null)
            itemViewHolder.itemView.getBackground().setColorFilter(mBackgroundColor.getDefaultColor(), PorterDuff.Mode.SRC_ATOP);
        if(mTextColor != null) {
            itemViewHolder.mLabel.setTextColor(mTextColor);
            itemViewHolder.mInfo.setTextColor(ColorUtil.alpha(mTextColor.getDefaultColor(), 150));
        }

        // onclick
        itemViewHolder.itemView.setOnClickListener(v -> {
                if(mChipsInput != null)
                    mChipsInput.addChip(chip);
            }
        );
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    private ChipInterface getItem(int position) {
        return mFilteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        if(mFilter == null)
            mFilter = new ChipFilter(mChipList);
        return mFilter;
    }

    private class ChipFilter extends Filter {

        private final List<ChipInterface> originalList;
        private final List<ChipInterface> filteredList;

        ChipFilter(List<ChipInterface> originalList) {
            super();
            this.originalList = originalList;
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChipInterface chip : originalList) {
                    if (chip.getLabel().toLowerCase().contains(filterPattern)) {
                        filteredList.add(chip);
                    }
                    else if(chip.getInfo() != null && chip.getInfo().toLowerCase().replaceAll("\\s", "").contains(filterPattern)) {
                        filteredList.add(chip);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFilteredList.clear();
            mFilteredList.addAll((ArrayList<ChipInterface>) results.values);
            notifyDataSetChanged();
        }
    }

    private void removeChip(ChipInterface chip) {
        int position = mFilteredList.indexOf(chip);
        if (position >= 0)
            mFilteredList.remove(position);

        position = mChipList.indexOf(chip);
        if(position >= 0)
            mChipList.remove(position);

        notifyDataSetChanged();
    }

    private void addChip(ChipInterface chip) {
        if(contains(chip)) {
            mChipList.add(chip);
            mFilteredList.add(chip);
            // sort original list
            Collections.sort(mChipList, (o1, o2) -> {
                Collator collator = Collator.getInstance(Locale.getDefault());
                collator.setStrength(Collator.PRIMARY);
                return collator.compare(o1.getLabel(), o2.getLabel());
            });
            // sort filtered list
            Collections.sort(mFilteredList, (o1, o2) -> {
                Collator collator = Collator.getInstance(Locale.getDefault());
                collator.setStrength(Collator.PRIMARY);
                return collator.compare(o1.getLabel(), o2.getLabel());
            });

            notifyDataSetChanged();
        }
    }

    private boolean contains(ChipInterface chip) {
        for(ChipInterface item: mOriginalList) {
            if(item.equals(chip))
                return true;
        }
        return false;
    }


}

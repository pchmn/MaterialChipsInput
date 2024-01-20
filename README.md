## [CURRENT SITUATION OF THE LIBRARY]

⚠️ The library is currently not maintained.

# MaterialChipsInput

Implementation of Material Design [Chips](https://material.io/guidelines/components/chips.html) component for Android. The library provides two views : [`ChipsInput`](#chipsinput) and [`ChipView`](#chipview).

[![Release](https://jitpack.io/v/pchmn/MaterialChipsInput.svg)](https://jitpack.io/#pchmn/MaterialChipsInput)

<img src="https://github.com/pchmn/MaterialChipsInput/blob/master/docs/demo2.gif" alt="Demo" height="600px"/>

## Demo
[Download sample-v1.0.8.apk](https://github.com/pchmn/MaterialChipsInput/raw/master/docs/material-chips-input-sample-v1.0.8.apk)

## Setup

To use this library your `minSdkVersion` must be >= 15.

In your project level build.gradle :
```java
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}       
```

In your app level build.gradle :
```java
dependencies {
    compile 'com.github.pchmn:MaterialChipsInput:1.0.8'
}      
```
<br><br>
## ChipsInput
This view implements the Material Design [Contact chips component](https://material.io/guidelines/components/chips.html#chips-contact-chips). 

It is composed of a collection of chips (`ChipView`) and an input (`EditText`). Touching a chip open a full detailed view (if non disable). The [GIF](#materialchipsinput) above describes the behavior of the `ChipsInput` view.

But everything is configurable (optional avatar icon, optional full detailed view, ...) so you can use the `ChipsInput` view for non contact chips.
### Basic Usage

#### XML
Use the ChipsInput view in your layout with default options  :

```xml
<com.pchmn.materialchips.ChipsInput
        android:id="@+id/chips_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hint="Enter a name" />
```
You can also customize it ([see](#chipsinput-attributes) all attributes) :
```xml
<com.pchmn.materialchips.ChipsInput
        android:id="@+id/chips_input"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hint="Enter a name"
        app:hintColor="@color/customColor"
        app:textColor="@color/customColor"
        app:maxRows="3"
        app:chip_labelColor="@color/customColor"
        app:chip_hasAvatarIcon="true"
        app:chip_backgroundColor="@color/customColor"
        app:chip_deletable="false"
        app:chip_deleteIconColor="@color/customColor"
        app:chip_detailed_textColor="@color/customColor"
        app:chip_detailed_backgroundColor="@color/customColor"
        app:chip_detailed_deleteIconColor="@color/customColor"
        app:filterable_list_backgroundColor="@color/customColor"
        app:filterable_list_textColor="@color/customColor" />
```

#### Suggestions
You can pass a `List<? extends ChipInterface>` object, which represents your suggestions, to the `ChipsInput` view, so it will work as a
`MultiAutoCompleteTextView` :

##### 1. Create a class that implements `ChipInterface` (or use directly the [`Chip`](https://github.com/pchmn/MaterialChipsInput/blob/master/library/src/main/java/com/pchmn/materialchips/model/Chip.java) class included in the library) :
```java
public class ContactChip implements ChipInterface {
    ...
}
```

##### 2. Then in your activity, or anything else, build your suggestion list of `ContactChip` (or `Chip`) and pass it to the `ChipsInput` view :
```java
// get ChipsInput view
ChipsInput chipsInput = (ChipsInput) findViewById(R.id.chips_input);

// build the ContactChip list
List<ContactChip> contactList = new ArrayList<>();
contactList.add(new ContactChip()); 
...

// pass the ContactChip list
chipsInput.setFilterableList(contactList);
```

#### Get the selected list
When you want you can get the current list of chips selected by the user :
```java
// get the list
List<ContactChip> contactsSelected = (List<ContactChip>) chipsInput.getSelectedChipList();
```

That's it, there is nothing more to do.
<br><br>
### Advanced Usage
#### ChipsListener
The `ChipsInput` view provides a listener to interact with the input :
```java
chipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                // chip added
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                // chip removed
                // newSize is the size of the updated selected chip list
            }

            @Override
            public void onTextChanged(CharSequence text) {
                // text changed
            }
        });
```

#### Add and remove chips manually
You don't have to pass a `List<? extends ChipInterface>` to the `ChipsInput` view and you can do the trick manually. Thanks to the `ChipsListener` you can be notified when the user is typing and do your own work.

```java
ChipsInput chipsInput = (ChipsInput) findViewById(R.id.chips_input);
```

##### Add a chip
There are multiple implementations :
```java
chipsInput.addChip(ChipInterface chip);
// or
chipsInput.addChip(Object id, Drawable icon, String label, String info);
// or
chipsInput.addChip(Drawable icon, String label, String info);
// or
chipsInput.addChip(Object id, Uri iconUri, String label, String info);
// or
chipsInput.addChip(Uri iconUri, String label, String info);
// or
chipsInput.addChip(String label, String info);
```

##### Remove a chip
There are multiple implementations :
```java
chipsInput.removeChip(ChipInterface chip);
// or
chipsInput.removeChipById(Object id);
// or
chipsInput.removeChipByLabel(String label);
// or
chipsInput.removeChipByInfo(String info);
```

After you added or removed a chip the `ChipsListener` will be triggered.

##### Get the selected list
When you want you can get the current list of chips selected by the user :
```java
// get the list
List<ChipInterface> contactsSelected = chipsInput.getSelectedChipList();
```

### ChipsInput attributes

Attribute | Type | Description | Default
--- | --- | --- | ---
`app:hint` | `string` | Hint of the input when there is no chip | null
`app:hintColor` | `color` | Hint color | android default 
`app:textColor` | `color` | Text color when user types | android default
`app:maxRows` | `int` | Max rows of chips | 2
`app:chip_labelColor` | `color` | Label color of the chips | android default
`app:chip_hasAvatarIcon` | `boolean` | Whether the chips have avatar icon or not | true
`app:chip_deletable` | `boolean` | Whether the chips are deletable (delete icon) or not | false
`app:chip_deleteIconColor` | `color` | Delete icon color of the chips | white/black
`app:chip_backgroundColor` | `color` | Background color of the chips | grey
`app:showChipDetailed` | `boolean` | Whether to show full detailed view or not when touching a chip | true
`app:chip_detailed_textColor` | `color` | Full detailed view text color | white/balck
`app:chip_detailed_backgroundColor` | `color` | Background color of the full detailed view | colorAccent
`app:chip_detailed_deleteIconColor` | `color` | Delete icon color of the full detailed view | white/black
`app:filterable_list_backgroundColor` | `color` | Background color of the filterable list of suggestions | white
`app:filterable_list_textColor` | `color` | Text color of the filterable list of suggestions | black

<br><br>
## ChipView
This view implements the chip component according to the [Material Design guidelines](https://material.io/guidelines/components/chips.html#chips-usage) with configurable options (background color, text color, ...).

<img src="https://github.com/pchmn/MaterialChipsInput/blob/master/docs/chips-examples.png" alt="Chips examples" width="50%"/>

### Usage
```xml
<com.pchmn.materialchips.ChipView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:label="Chip 1" />
            
<com.pchmn.materialchips.ChipView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:label="Chip 4"
                app:hasAvatarIcon="true" />

<com.pchmn.materialchips.ChipView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:label="Chip 6"
                app:labelColor="@android:color/white"
                app:avatarIcon="@drawable/avatar"
                app:backgroundColor="@android:color/holo_blue_light"
                app:deletable="true"
                app:deleteIconColor="@android:color/white" />
    
```

### ChipView attributes

Attribute | Type | Description | Default
--- | --- | --- | ---
`app:label` | `string` | Label of the chip | null
`app:labelColor` | `color` | Label color of the chip | android default
`app:hasAvatarIcon` | `boolean` | Whether the chip has avatar icon or not | false
`app:avatarIcon` | `drawable` | Avatar icon resource | null
`app:deletable` | `boolean` | Whether the chip is deletable (delete icon) or not | false
`app:deleteIconColor` | `color` | Delete icon color of the chip | grey
`app:backgroundColor` | `color` | Background color of the chip | grey

### Listeners
```java
ChipView chip = (ChipView) findViewById(R.id.chip_view);
```

On chip click listener :
```java
chip.setOnChipClicked(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        // handle click    
    }
});
```

On delete button click listener :
```java
chip.setOnDeleteClicked(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        // handle click     
    }
});
```
<br><br>
## Sample

A sample app with some use cases of the library is available on this [link](https://github.com/pchmn/MaterialChipsInput/tree/master/sample)

You can also download the sample APK [here](https://github.com/pchmn/MaterialChipsInput/raw/master/docs/material-chips-input-sample-v1.0.0.apk)

## Credits

* [Android Material Chips](https://github.com/DoodleScheduling/android-material-chips)
* [Material Chip View](https://github.com/robertlevonyan/materialChipView?utm_source=android-arsenal.com&utm_medium=referral&utm_campaign=5396)
* [ChipsLayoutManager](https://github.com/BelooS/ChipsLayoutManager)

## License

```
Copyright 2017 pchmn

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

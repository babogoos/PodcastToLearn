package com.dionchang.podcasttolearn.ui.home

import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState


/**
 * Created by dion on 2023/06/24.
 */

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExposedDropdownMenuSample(
    options: List<String>,
    expanded: MutableState<Boolean>,
    selectedOptionText: MutableState<String>,
    onSelectIndex: (Int) -> Unit
) {
    // We want to react on tap/press on TextField to show menu
    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = {
            expanded.value = !expanded.value
        }
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText.value,
            onValueChange = { },
            label = { Text("Choose Podcat Channel") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded.value
                )
            },
            // Fixme: The colors are wired after the any selection.
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            options.forEachIndexed { index, selectionOption ->
                DropdownMenuItem(
                    onClick = {
                        selectedOptionText.value = selectionOption
                        expanded.value = false
                        onSelectIndex.invoke(index)
                    }
                ) {
                    Text(text = selectionOption)
                }
            }
        }
    }
}

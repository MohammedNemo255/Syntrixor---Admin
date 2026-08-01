package com.syntrixor.syntrixoradmin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(hint) },
        leadingIcon = {
            Icon(Icons.Filled.Search, contentDescription = null, tint = Violet600)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = null)
                }
            }
        },
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.RadiusLg),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Violet600,
            focusedLeadingIconColor = Violet600,
            unfocusedBorderColor = Color.Transparent,
            unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        )
    )
}

package com.syntrixor.syntrixoradmin.ui.screens.requests

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import com.syntrixor.syntrixoradmin.ui.components.RequestCard
import com.syntrixor.syntrixoradmin.ui.components.SearchField
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

data class FilterOption(val labelRes: Int, val status: RequestStatus?)

val filterOptions = listOf(
    FilterOption(R.string.filter_all,         null),
    FilterOption(R.string.filter_pending,     RequestStatus.PENDING),
    FilterOption(R.string.filter_in_progress, RequestStatus.IN_PROGRESS),
    FilterOption(R.string.filter_completed,   RequestStatus.COMPLETED),
    FilterOption(R.string.filter_cancelled,   RequestStatus.CANCELLED),
)

@Composable
fun RequestsScreen(
    modifier: Modifier = Modifier,
    onRequestClick: (String) -> Unit,
    vm: RequestsViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search bar
        SearchField(
            query = state.searchQuery,
            onQueryChange = { vm.setSearch(it) },
            hint = stringResource(R.string.search_requests),
            modifier = Modifier.padding(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.SpaceSm)
        )

        // Filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.SpaceXs),
            horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
        ) {
            items(filterOptions) { option ->
                val selected = state.activeFilter == option.status
                FilterChip(
                    selected = selected,
                    onClick  = { vm.setFilter(option.status) },
                    label    = { Text(stringResource(option.labelRes)) },
                    colors   = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Violet600,
                        selectedLabelColor     = Color.White
                    )
                )
            }
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Violet600)
            }
            state.error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error ?: "", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { vm.load() },
                        colors  = ButtonDefaults.buttonColors(containerColor = Violet600)
                    ) { Text(stringResource(R.string.retry), color = Color.White) }
                }
            }
            state.requests.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.no_requests), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = Dimens.ScreenPaddingHorizontal, vertical = Dimens.SpaceSm),
                verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
            ) {
                items(state.requests) { request ->
                    RequestCard(request = request, onClick = { onRequestClick(request.id) })
                }
            }
        }
    }
}

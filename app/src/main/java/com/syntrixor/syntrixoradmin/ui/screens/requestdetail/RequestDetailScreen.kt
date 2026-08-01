package com.syntrixor.syntrixoradmin.ui.screens.requestdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.syntrixor.syntrixoradmin.R
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import com.syntrixor.syntrixoradmin.ui.components.StatusBadge
import com.syntrixor.syntrixoradmin.ui.components.SyntrixorTopBar
import com.syntrixor.syntrixoradmin.ui.theme.Dimens
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.Violet600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    requestId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    vm: RequestDetailViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return RequestDetailViewModel(requestId) as T
        }
    })
) {
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            vm.onEvent(RequestDetailEvent.ClearMessage)
        }
    }
    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            vm.onEvent(RequestDetailEvent.ClearMessage)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { SyntrixorTopBar(title = stringResource(R.string.request_detail_title), onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) { data ->
            Snackbar(
                containerColor = Success,
                contentColor = Color.White,
                snackbarData = data
            )
        }}
    ) { innerPadding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Violet600)
            }
            state.request == null -> Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.request_not_found), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> {
                val req = state.request!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(Dimens.ScreenPaddingHorizontal),
                    verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLg)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.RadiusMd))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(Dimens.SpaceLg)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(req.title, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                StatusBadge(req.status)
                            }
                            Spacer(Modifier.height(Dimens.SpaceSm))
                            Text(req.description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Dimens.RadiusMd))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(Dimens.SpaceLg),
                            verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)
                        ) {
                            DetailRow(stringResource(R.string.request_id),   req.id)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            DetailRow(stringResource(R.string.submitted_by), req.residentName)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            DetailRow(stringResource(R.string.unit),         req.unit)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            DetailRow(stringResource(R.string.category),     stringResource(req.category.labelRes))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            DetailRow(stringResource(R.string.priority),     stringResource(req.priority.labelRes))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            DetailRow(stringResource(R.string.submitted_on), req.submittedAt.take(10))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            DetailRow(stringResource(R.string.assigned_to),  req.assignedTechnicianName ?: stringResource(R.string.unassigned))
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
                            OutlinedButton(
                                onClick = { vm.onEvent(RequestDetailEvent.ShowAssignDialog) },
                                enabled = !state.isUpdating,
                                modifier = Modifier.weight(1f).height(Dimens.ButtonHeight),
                                shape = RoundedCornerShape(Dimens.RadiusMd),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Violet600)
                            ) {
                                if (state.isUpdating) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Violet600)
                                } else {
                                    Text(stringResource(R.string.assign_technician), color = Violet600, fontSize = 13.sp)
                                }
                            }
                            Button(
                                onClick = { vm.onEvent(RequestDetailEvent.ShowStatusDialog) },
                                enabled = !state.isUpdating,
                                modifier = Modifier.weight(1f).height(Dimens.ButtonHeight),
                                shape = RoundedCornerShape(Dimens.RadiusMd),
                                colors = ButtonDefaults.buttonColors(containerColor = Violet600)
                            ) {
                                Text(stringResource(R.string.update_status), color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        // Assign Technician dialog
        if (state.showAssignDialog) {
            AlertDialog(
                onDismissRequest = { vm.onEvent(RequestDetailEvent.HideAssignDialog) },
                title = { Text(stringResource(R.string.assign_technician)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
                        state.technicians.filter { it.isActive }.forEach { tech ->
                            TextButton(onClick = { vm.onEvent(RequestDetailEvent.AssignTechnician(tech.id)) }, modifier = Modifier.fillMaxWidth()) {
                                Column(Modifier.fillMaxWidth()) {
                                    Text(tech.name, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("${tech.specialization}  •  ${tech.openRequestsCount} ${stringResource(R.string.open_word)}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { vm.onEvent(RequestDetailEvent.HideAssignDialog) }) { Text(stringResource(R.string.cancel)) } }
            )
        }

        // Update Status dialog
        if (state.showStatusDialog) {
            AlertDialog(
                onDismissRequest = { vm.onEvent(RequestDetailEvent.HideStatusDialog) },
                title = { Text(stringResource(R.string.update_status)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
                        RequestStatus.entries.forEach { status ->
                            TextButton(onClick = { vm.onEvent(RequestDetailEvent.UpdateStatus(status)) }, modifier = Modifier.fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(stringResource(status.labelRes), color = MaterialTheme.colorScheme.onSurface)
                                    StatusBadge(status)
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = { TextButton(onClick = { vm.onEvent(RequestDetailEvent.HideStatusDialog) }) { Text(stringResource(R.string.cancel)) } }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}

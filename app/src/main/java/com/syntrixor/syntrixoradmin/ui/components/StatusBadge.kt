package com.syntrixor.syntrixoradmin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syntrixor.syntrixoradmin.data.model.RequestStatus
import com.syntrixor.syntrixoradmin.ui.theme.Error
import com.syntrixor.syntrixoradmin.ui.theme.Info
import com.syntrixor.syntrixoradmin.ui.theme.Success
import com.syntrixor.syntrixoradmin.ui.theme.Warning

@Composable
fun StatusBadge(status: RequestStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        RequestStatus.PENDING     -> Warning.copy(alpha = 0.15f) to Warning
        RequestStatus.IN_PROGRESS -> Info.copy(alpha = 0.15f)    to Info
        RequestStatus.COMPLETED   -> Success.copy(alpha = 0.15f) to Success
        RequestStatus.CANCELLED   -> Error.copy(alpha = 0.15f)   to Error
    }
    Text(
        text = stringResource(status.labelRes),
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

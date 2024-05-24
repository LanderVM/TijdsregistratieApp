package world.inetumrealdolmen.mobiletrm.ui.common.indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import world.inetumrealdolmen.mobiletrm.R
import world.inetumrealdolmen.mobiletrm.data.model.ErrorType
import world.inetumrealdolmen.mobiletrm.ui.common.icon.ErrorIcon
import world.inetumrealdolmen.mobiletrm.ui.common.icon.InfoIcon
import world.inetumrealdolmen.mobiletrm.ui.common.icon.LoadingIcon

/**
 * An indicator, uses an icon from [world.inetumrealdolmen.mobiletrm.ui.common.icon] based off [type].
 *
 * @param type The type of indicator.
 * @param modifier Default Compose [Modifier].
 * @param text  Optional custom text to display. Defaults to a message based off [type] if blank.
 * @param error The message to display if no [text] is provided, based on the provided [ErrorType].
 * @param isTextOnly Only shows text if true. Default is false.
 * @param isIconOnly Only shows the icon if true. Default is false.
 * @param iconSize The size of the icon in dp. Default is 64.
 * @param useEntireScreen Whether to take up the entire screen. Default is true.
 */
@Composable
fun Indicator(
    type: IndicatorType,
    modifier: Modifier = Modifier,
    text: String = "",
    error: ErrorType = ErrorType.Unmapped,
    isTextOnly: Boolean = false,
    isIconOnly: Boolean = false,
    iconSize: Int = 64,
    useEntireScreen: Boolean = true,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val fullModifier =
        modifier
            .fillMaxSize()
            .padding(16.dp)

    Column(
        modifier =
            if (useEntireScreen) {
                fullModifier
            } else {
                modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!isTextOnly) {
            when (type) {
                IndicatorType.LOADING -> LoadingIcon(size = iconSize)
                IndicatorType.ERROR -> ErrorIcon(size = iconSize)
                IndicatorType.INFO -> InfoIcon(size = iconSize)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (!isIconOnly) {
            Text(
                text =
                    text.ifBlank {
                        when (type) {
                            IndicatorType.LOADING -> stringResource(id = R.string.loading)
                            IndicatorType.INFO -> stringResource(id = R.string.info)
                            IndicatorType.ERROR ->
                                when (error) {
                                    is ErrorType.SocketTimeout -> stringResource(R.string.error_socketTimeout)
                                    is ErrorType.NotFound -> stringResource(R.string.error_notFound)
                                    is ErrorType.Internal -> stringResource(R.string.error_internal)
                                    is ErrorType.Unmapped -> stringResource(R.string.error_unknown)
                                    is ErrorType.BadRequest -> stringResource(R.string.error_badRequest)
                                    is ErrorType.Unknown ->
                                        if (error.errorMessage == null) {
                                            stringResource(R.string.error_unknown)
                                        } else {
                                            stringResource(
                                                R.string.error,
                                                error.errorMessage,
                                            )
                                        }
                                }
                        }
                    },
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }
        content()
    }
}

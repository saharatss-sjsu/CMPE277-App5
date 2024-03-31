import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.cmpe277_app5.R
import com.example.cmpe277_app5.database.ChatRecord
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SetTextI18n")
class ChatRecordRow @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    @RequiresApi(Build.VERSION_CODES.O)
    var chatRecord = ChatRecord("?", LocalDateTime.now(), true, "No Content")
        @RequiresApi(Build.VERSION_CODES.O)
        set(value) {
            field = value
            textView1.text = "${chatRecord.datetime}"
            textView2.text = if (chatRecord.isPrompt) "ME" else "AI"
            textView3.text = chatRecord.content
        }


    private val textView1: TextView
    private val textView2: TextView
    private val textView3: TextView

    init {
        orientation = HORIZONTAL
        setPadding(0, 0, 0, 24)
        gravity = Gravity.CENTER_VERTICAL

        textView1 = TextView(context).apply {
            setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Caption)
            text = "${chatRecord.datetime}"
        }

        textView2 = TextView(context).apply {
            setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Body2)
            text = if (chatRecord.isPrompt) "ME" else "AI"
            setPadding(0,0,16,0)
            setTextColor(context.getColor(R.color.purple_500))
        }

        textView3 = TextView(context).apply {
            setTextAppearance(androidx.appcompat.R.style.TextAppearance_AppCompat_Body1)
            text = chatRecord.content
        }

        addView(LinearLayout(context).apply {
            orientation = VERTICAL
            addView(textView1)
            addView(LinearLayout(context).apply {
                orientation = HORIZONTAL
                addView(textView2)
                addView(textView3)
            })
        })

    }
}

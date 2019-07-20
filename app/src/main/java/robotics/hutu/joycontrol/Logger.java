package robotics.hutu.joycontrol;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Вспомогательный класс для лога, в дальнейшем это может позволить писать автоматически логи в файл
 */
public class Logger {
    private String tag;
    private Context context;
    public String getTag(){
        return tag;
    }
    public void setTag(String tag){
        this.tag = tag;
    }
    public Logger(Context context, String tag){
        this.tag = tag;
        this.context = context;
    }
    public void log(String msg){
        Log.d(tag, msg);
    }
}

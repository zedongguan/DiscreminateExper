package robot.tangwa.discreminateexper.FaceRecognizer;

import android.graphics.Point;
import android.graphics.Rect;

/** FaceRect是用于表示人脸检测的结果，其中包括了 人脸的角度、得分、检测框位置、关键点
 * Created by Administrator on 2017/3/20.
 */

public class FaceRect {
    public float score;

    public Rect bound = new Rect();
    public Point point[];

    public Rect raw_bound = new Rect();
    public Point raw_point[];

    @Override
    public String toString() {
        return bound.toString();
    }


}

# GesturePwdView

## gradle 导入

```
     implementation 'com.github.lwjfork:GestureLockView:1.0.5'
```

## 属性使用

属性名 | 属性说明
:---:|:---:
selectDrawable |  正常状态下的Drawable资源
normalDrawable |  选中时Drawable资源
errorDrawable |   校验错误的Drawable资源
rowNums |   行数
columnNums |  列数
dotWidth |   每个点图片的宽度
dotHeight |   每个点图片的高度
spaceH |   点与点的水平间距
spaceV |   点与点的竖直间距
lineColor |   绘制时--线条颜色
lineErrorColor |   校验错误时-线条颜色
lineWidth |   线条的宽度
autoSelectMiddle |   当选中两点时，是否自动选中同一条线上未选中的点
delayClearTime | 当绘制完成后-所停留时长恢复状态

## GestureLockView
> 手势密码View，可以跟随手指触摸点自动绘制

### 解码/编码示例

    

     public class OnCodeAdapter implements GestureLockView.OnCodeConvertAdapter<String, String> {

        // 根据 setOldPath 设置的路径，转换得到实际的选中路径
        @Override
        public ArrayList<Integer> convertObj2Code(String oldCode) {
            ArrayList<Integer> codes = new ArrayList<>();
            String[] array = oldCode.split(",");
            for (String s : array) {
                codes.add(Integer.valueOf(s));
            }
            return codes;
        }

       // 将用户绘制的路径转换为所要保存的类型
        @Override
        public String convertCode2Obj(ArrayList<Integer> code) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Integer integer : code) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(integer);
            }
            return stringBuilder.toString();
        }
    }


    GestureLockView lockView = findViewById(R.id.lockView);

    lockView..setOnCodeConvertAdapter(new OnCodeAdapter())；

    lockView.setOnGestureCallBackListener(new GestureLockView.OnGestureCallBackListener<String>() {

            @Override
            public void onGestureCodeInput(ArrayList<Integer> code, String inputCode) {
                Log.e("inputCode", inputCode);
              // 设置指示器
              lockViewIndicator.setPath(inputCode);
           
            }

            @Override
            public void onCheckedSuccess(ArrayList<Integer> code, String rightCode) {
               Log.e("rightCode", rightCode);
            }

            @Override
            public void onCheckedFail(ArrayList<Integer> code, String errorCode) {
               Log.e("errorCode", errorCode);
            }
        });
    
    


## GestureLockViewIndicator
> 手势密码指示器，可以配合 GestureLockView 显示此次用户绘制的手势密码


### 解码/编码示例

    public class OnCodeAdapter implements GestureLockViewIndicator.OnDecodeAdapter<String> {

        // 根据 setPath 设置的路径，转换得到实际的选中路径
         @Override
        public ArrayList<Integer> decodePath(String object) {
            ArrayList<Integer> codes = new ArrayList<>();
            String[] array = oldCode.split(",");
            for (String s : array) {
                codes.add(Integer.valueOf(s));
            }
            return codes;
        }
    }


    GestureLockViewIndicator lockViewIndicator = (GestureLockViewIndicator) findViewById(R.id.lock_indicator);

    lockViewIndicator..setOnCodeConvertAdapter(new OnCodeAdapter())；
    

# 代码混淆

    -keep class com.lwjfork.widget.**{*;}





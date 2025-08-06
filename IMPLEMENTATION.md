# تطبيق Chess Overlay - دليل التطبيق

## نظرة عامة
تطبيق أندرويد عائم يساعد في لعب الشطرنج على chess.com من خلال تحليل الشاشة واقتراح أفضل النقلات باستخدام محرك Stockfish.

## المكونات الرئيسية

### 1. MainActivity
- واجهة المستخدم الرئيسية
- إدارة الأذونات المطلوبة
- تشغيل وإيقاف الخدمة العائمة

### 2. OverlayService
- خدمة تعمل في الخلفية
- إنشاء الطبقة العائمة فوق التطبيقات الأخرى
- إدارة الزر العائم القابل للسحب

### 3. ChessAnalyzer
- تحليل وضع الشطرنج الحالي
- التقاط لقطة من الشاشة
- تنسيق العمليات بين مكونات التحليل

### 4. ChessBoardDetector
- استخدام OpenCV لتحليل صورة الشاشة
- اكتشاف رقعة الشطرنج ومواقع القطع
- تحويل المواقع إلى تنسيق FEN

### 5. StockfishEngine
- تشغيل محرك الشطرنج Stockfish
- تحليل الوضع واقتراح أفضل نقلة
- إدارة عملية UCI protocol

## المتطلبات التقنية

### الأذونات المطلوبة
- `SYSTEM_ALERT_WINDOW` - للطبقة العائمة
- `FOREGROUND_SERVICE` - للخدمة في الخلفية
- `FOREGROUND_SERVICE_MEDIA_PROJECTION` - لالتقاط الشاشة

### التبعيات
- OpenCV for Android - معالجة الصور
- Stockfish Engine - تحليل الشطرنج
- Android Support Libraries

## خطوات التطبيق

### 1. إعداد الطبقة العائمة
```kotlin
// إنشاء نافذة عائمة
val layoutParams = WindowManager.LayoutParams(
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.WRAP_CONTENT,
    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
    PixelFormat.TRANSLUCENT
)
```

### 2. التقاط الشاشة
```kotlin
// استخدام MediaProjection API
val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
val mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
```

### 3. تحليل الصورة
```kotlin
// تحويل إلى OpenCV Mat
val mat = Mat()
Utils.bitmapToMat(screenshot, mat)

// اكتشاف رقعة الشطرنج
val boardCorners = detectChessboardCorners(mat)
```

### 4. تحليل النقلات
```kotlin
// تشغيل Stockfish
stockfishProcess = ProcessBuilder(stockfishFile.absolutePath).start()
writer.write("position fen $fen\n")
writer.write("go depth 15\n")
```

## التحديات التقنية

### 1. اكتشاف رقعة الشطرنج
- تحديد حدود الرقعة في الصورة
- التعامل مع زوايا مختلفة وإضاءة متغيرة
- تمييز القطع والمربعات الفارغة

### 2. تحليل القطع
- تصنيف نوع كل قطعة (ملك، ملكة، إلخ)
- تحديد لون القطعة (أبيض/أسود)
- التعامل مع أشكال القطع المختلفة

### 3. تحويل إلى FEN
- ترجمة مواقع القطع إلى تنسيق FEN
- تحديد الدور (أبيض أم أسود)
- معلومات إضافية (التبييت، en passant)

### 4. عرض النقلة المقترحة
- رسم مربعات ملونة فوق الرقعة
- محاذاة دقيقة مع مربعات الشطرنج
- تحديث الموقع عند تحرك الرقعة

## الاستخدام

1. تشغيل التطبيق ومنح الأذونات المطلوبة
2. فتح تطبيق chess.com
3. الضغط على "تشغيل الطبقة العائمة"
4. سحب الزر العائم إلى موقع مناسب
5. الضغط على "اقتراح نقلة" لتحليل الوضع
6. مشاهدة النقلة المقترحة على الشاشة

## ملاحظات التطوير

### المرحلة الحالية
- تم إنشاء الهيكل الأساسي للتطبيق
- تم تطبيق واجهات المستخدم والخدمات
- تم إعداد تكامل OpenCV و Stockfish

### المطلوب للإكمال
- تطبيق خوارزميات اكتشاف رقعة الشطرنج
- إضافة ملف Stockfish binary
- تطبيق نظام عرض النقلات المقترحة
- اختبار شامل مع تطبيق chess.com

### التحسينات المستقبلية
- تحسين دقة اكتشاف القطع
- دعم أشكال رقع مختلفة
- إضافة إعدادات قابلة للتخصيص
- تحسين الأداء وتقليل استهلاك البطارية

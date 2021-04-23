## HouseKeepingBook  
가계 수입과 지출을 중심으로 재산의 증가와 감소를 일정한 형식에 맞추어서 회기 동안 기록하는 장부 어플입니다.  
**작성자 본인의 역량 향상을 제일 우선시 하며 적은 본인을 위한 작성입니다.**  
**현재 진행중인 프로젝트입니다.**  
### DB 쓰기
`getWritableDatabase()` , `getReadableDatabase()`  
 : 데이터베이스를 생성하거나 여는데 사용합니다.
```java
ContentValues contentValues = new ContentValues(); // 데이터를 담을 객체 생성
contentValues.put(DBStructure.COLUMN_NAME_DAY,Today); // 입력 날짜
contentValues.put(DBStructure.COLUMN_NAME_INCOME,Income); // 금액
contentValues.put(DBStructure.COLUMN_NAME_TAGNAME,strTagName); // 카테고리
SQLiteDatabase db = DBHelper.getInstance(getActivity()).getWritableDatabase();
db.insert(DBStructure.TABLE_NAME,null,contentValues); // 쓰기
```  
### DB 읽기  
```java
DBHelper dbHelper = DBHelper.getInstance(context);  // SQLiteOpenHelper
String SQL_SELECT = "SELECT * FROM " + DBStructure.TABLE_NAME +
                " WHERE " + DBStructure.COLUMN_NAME_DAY +
                " LIKE '%" + Year + "%"+ Month +"%';";
cursor = dbHelper.getReadableDatabase().rawQuery(SQL_SELECT,null);
cursor.getString(i) // i번 인덱스에 해당하는 칼럼에 저장값을 가져옵니다.
```
### 날짜 세팅  
`Calendar` 와 `SimpleDateFormat` 으로 원하는 형태로 현재 시간을 갖고옵니다.  
`DatePickerDialog` 를 활용하여 이후 사용자가 원하는 날짜를 직접 세팅할수 있도록 하였습니다.  
달력 형태가 아닌 보다 빠른 날짜 선택이 가능하도록 스크롤이 가능한 Spinner 형태로 구현했습니다.
```java
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월", Locale.KOREAN);
Calendar calendar = Calendar.getInstance(Locale.KOREAN);
String currentDate = dateFormat.format(calendar.getTime());
```
`listener` : Dialog 창의 확인 버튼을 클릭 했을 때 실행될 CallBack 함수.   
`mYear`,`mMonth`,`mDay` : Dialog 를 띄울 때 세팅될 날짜입니다.  
```java
DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        listener,mYear,mMonth,mDay);
datePickerDialog.show();
private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view1, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
        }
    };
```
### MPAndroidChart 의 PieChart  
MPandroidChart 라이브러리를 활용하여 PieChart로 원 그래프를 구현했습니다.  
참고 [https://github.com/PhilJay/MPAndroidChart](here)  


## 사진  
<img src="https://github.com/theiogh/HouseKeepingBook/blob/master/Main.jpg" width="350" height="530" /> <img src="https://github.com/theiogh/HouseKeepingBook/blob/master/DatePickerDialog.jpg" width="350" height="530"/><img src="https://github.com/theiogh/HouseKeepingBook/blob/master/Input.jpg" width="350" height="530"/><img src="https://github.com/theiogh/HouseKeepingBook/blob/master/Breakdown.jpg" width="350" height="530"/><img src="https://github.com/theiogh/HouseKeepingBook/blob/master/BreakdownDialog.jpg" width="350" height="530"/><img src="https://github.com/theiogh/HouseKeepingBook/blob/master/Calendar.jpg" width="350" height="530"/><img src="https://github.com/theiogh/HouseKeepingBook/blob/master/CalendarDialog.jpg" width="350" height="530"/>


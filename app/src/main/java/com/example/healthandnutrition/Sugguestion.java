package com.example.healthandnutrition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Sugguestion extends AppCompatActivity {
    DatabaseHelper dh;
    SQLiteDatabase db;
    LinearLayout parentLayout;
    String bp ="";
    String get_id ="";
    String st1 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugguestion);

        dh = new DatabaseHelper(this);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        Bundle bundle = getIntent().getExtras();
        if (bundle !=null) {
            try {

                bp = bundle.getString("bp");
                get_id = bundle.getString("get_id");

            }  catch (Exception ex) {
                message("Error", ex.toString());
            }

        }


        display_all();

    }

    private void display_all(){
        try {
            parentLayout.removeAllViews();
            this.db = this.dh.getWritableDatabase();
            Cursor result = db.rawQuery("SELECT * FROM "+ this.dh.RECOMMENDATIONS +" WHERE bp ='"+bp+"' and bp_id = '"+get_id+"' ",null);

            if (result.getCount() <= 0) {

                in_();

            } else {
                while (result.moveToNext()) {


                    final Holder holder = new Holder();

                    final View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_sugguest, null);
                    holder.t_day = view.findViewById(R.id.t_day);
                    holder.t_btn_down = view.findViewById(R.id.t_btn_down);
                    holder.t_btn_up = view.findViewById(R.id.t_btn_up);
                    holder.t_description = view.findViewById(R.id.t_description);
                    holder.r_details = view.findViewById(R.id.r_details);
                    holder.ck_day = view.findViewById(R.id.ck_day);
                    String _id_ = result.getString(0);
                    holder.t_day.setText("Day "+result.getString(2));
                    holder.t_description.setText(result.getString(3));

                    holder.t_btn_down.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.t_btn_up.setVisibility(View.VISIBLE);
                            holder.t_btn_down.setVisibility(View.GONE);
                            holder.r_details.setVisibility(View.VISIBLE);
                        }
                    });
                    holder.t_btn_up.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.t_btn_up.setVisibility(View.GONE);
                            holder.t_btn_down.setVisibility(View.VISIBLE);
                            holder.r_details.setVisibility(View.GONE);
                        }
                    });


                    if (result.getString(5).equals("1")){
                        holder.ck_day.setChecked(true);
                        st1 = "0";
                    }else{
                        holder.ck_day.setChecked(false);
                        st1 = "1";
                    }

                    holder.ck_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String st = "";
                            if(holder.ck_day.isChecked())
                            {
                                st = "1";
                            }
                            else
                            {
                                st = "0";
                            }
                            String finalSt = st;
                            try {
                                Sugguestion.this.db = Sugguestion.this.dh.getWritableDatabase();
                                Sugguestion.this.db.execSQL(" UPDATE " + Sugguestion.this.dh.RECOMMENDATIONS + " SET status = ? WHERE id = ?", new String[]{finalSt,_id_});

                                Cursor cursor_2 = Sugguestion.this.db.rawQuery("SELECT * FROM " + Sugguestion.this.dh.RECOMMENDATIONS +" WHERE status = '1'",null);

                                if (cursor_2.getCount() == 7) {

                                    Sugguestion.this.db.execSQL("UPDATE " + Sugguestion.this.dh.BP_RESULT + " SET status = ? WHERE id = ?", new String[]{"1",get_id});

                                }else{
                                    Sugguestion.this.db.execSQL("UPDATE " + Sugguestion.this.dh.BP_RESULT + " SET status = ? WHERE id = ?", new String[]{"0",get_id});

                                }


                            }catch (SQLiteException e){
                                message("SQL Error", e.toString());
                            }catch (Exception e){
                                message("Error", e.toString());
                            }

                        }
                    });
                    parentLayout.addView(view);
                }

            }
        }catch (SQLiteException e){
            message("SQL Error", e.toString());
        }
        catch (Exception e){
            message("Error", e.toString());
        }
    }

    private class Holder {
        TextView t_day,t_btn_down,t_btn_up,t_description;
        RelativeLayout r_details;
        CheckBox ck_day;

    }
    public void in_(){

        try {
            if (bp.equals("2")){
                insert_mild(get_id);
            }else if (bp.equals("3")){
                insert_moderate(get_id);
            }else if (bp.equals("4")){
                insert_severre(get_id);
            }
        }catch (Exception e){
            message("Error",e.toString());
        }
        display_all();
    }

    public void message(String title, String message){
        final AlertDialog.Builder builder= new AlertDialog.Builder(Sugguestion.this);
        final View customLayout = LayoutInflater.from(Sugguestion.this).inflate(R.layout.alert_layout, null);
        builder.setView(customLayout);
        final TextView txt_title = customLayout.findViewById(R.id.txt_title);
        final TextView txt_message = customLayout.findViewById(R.id.txt_message);
        final TextView txt_close = customLayout.findViewById(R.id.txt_close);
        txt_title.setText(title);
        txt_message.setText(message);

        final AlertDialog dialog= builder.create();

        txt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finishAffinity();
    }
    public void onBack_S(View view){

        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_right);
        finishAffinity();
    }


    public  void insert_mild(String bp_id){
        db = dh.getWritableDatabase();
        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','1','Breakfast \n" +
                "Egg Toast with Salsa\n" +
                "•   1 slice whole-wheat bread, toasted\n" +
                "•   1 egg, cooked in 1/4 tsp. olive oil\n" +
                "•   Pinch each of salt and pepper\n" +
                "•   2 Tbsp. pico de gallo or salsa\n" +
                "•   1 medium banana\n" +
                "A.M. Snack \n" +
                "•   1 pear, sliced topped with cinnamon\n" +
                "Lunch \n" +
                "•   1 serving Veggie-Hummus Sandwich\n" +
                "P.M. Snack\n" +
                "•   3/4 cup raspberries\n" +
                "Dinner \n" +
                "•   1 serving Lemon-Herb Salmon with Caponata & Farro\n','','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','2','Breakfast \n" +
                "•   2/3 cup nonfat plain Greek yogurt\n" +
                "•   5 dried figs, chopped\n" +
                "•   2 tsp. chia seeds\n" +
                "•   1 1/2 tsp. honey\n" +
                "A.M. Snack \n" +
                "•   1/2 cup grapes\n" +
                "Lunch \n" +
                "•   2 cups mixed greens\n" +
                "•   3/4 cup chopped veggies, such as cucumber and cherry tomatoes\n" +
                "•   1/3 cup canned white beans, rinsed\n" +
                "•   1/2 avocado, diced\n" +
                "•   2 Tbsp. All-Purpose Vinaigrette\n" +
                "P.M. Snack \n" +
                "•   1 clementine\n" +
                " Dinner \n" +
                "•   1 serving Curried Cauliflower Steaks with Red Rice & Tzatziki\n" +
                "•   1 serving Chocolate & Nut Butter Bites, to enjoy after dinner\n','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','3','Breakfast \n" +
                "•   1 serving Peanut-Butter Cinnamon Toast\n" +
                "A.M. Snack \n" +
                "•   1 cup raspberries\n" +
                "Lunch \n" +
                "•   1 serving Salmon Pita Sandwich (save the other half of the pita for lunch on Day 5)\n" +
                "•   1 cup grapes\n" +
                "P.M. Snack \n" +
                "•   1 medium pear, sliced and topped with cinnamon\n" +
                "Dinner \n" +
                "•   1 serving Mediterranean Chicken with Orzo Salad\n" +
                "•   1 clementine, to enjoy after dinner\n" +
                "Daily Totals: 1,212 calories, 69 g protein, 164 g carbohydrates, 30 g fiber, 35 g fat, 1,234 mg sodium','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','4','Breakfast \n" +
                "Yogurt with Nuts & Raspberries\n" +
                "•   1 cup nonfat plain Greek yogurt\n" +
                "•   1/2 cup raspberries\n" +
                "•   5 walnuts, chopped\n" +
                "•   1 tsp. honey\n" +
                "Top yogurt with raspberries, walnuts and honey.\n" +
                "A.M. Snack \n" +
                "•   1 medium apple, sliced sprinkled with cinnamon\n" +
                "Lunch \n" +
                "•   1 serving White Bean & Avocado Toast\n" +
                "•   1 1/2 cups mixed greens\n" +
                "•   1/2 cup cucumber slices\n" +
                "•   2 Tbsp. grated carrot\n" +
                "•   1 Tbsp. All-Purpose Vinaigrette\n" +
                "P.M. Snack (30 calories)\n" +
                "•   1 medium plum\n" +
                "Dinner \n" +
                "•   1 serving Stuffed Sweet Potato with Hummus Dressing\n" +
                "Daily Totals: 1,181 calories, 58 g protein, 176 g carbohydrates, 46 g fiber, 36 g fat, 976 mg sodium','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','5','Breakfast \n" +
                "•   1 serving Peanut-Butter Cinnamon Toast\n" +
                "A.M. Snack \n" +
                "•   2 clementines\n" +
                "Lunch \n" +
                "2 cups mixed greens\n" +
                "•   1/4 cup grated carrot\n" +
                "•   1/2 cup sliced cucumber\n" +
                "•   2 Tbsp. All-Purpose Vinaigrette\n" +
                "•   1/2 large whole-wheat pita round\n" +
                "•   1/4 cup hummus\n" +
                "P.M. Snack (104 calories)\n" +
                "•   1 cup grapes\n" +
                "Dinner (412 calories)\n" +
                "•   1 1/2 cups Chicken Chili with Sweet Potatoes\n" +
                "•   1/4 avocado, diced\n" +
                "•   1 Tbsp. nonfat plain Greek yogurt\n','','0','"+bp_id+"')");


        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','6','Breakfast \n" +
                "Fig & Honey Yogurt\n" +
                "•   2/3 cup nonfat plain Greek yogurt\n" +
                "•   5 dried figs, chopped\n" +
                "•   2 tsp. chia seeds\n" +
                "•   1 1/2 tsp. honey\n" +
                "A.M. Snack \n" +
                "•   1 cup raspberries\n" +
                "•   Lunch (342 calories)\n" +
                "Turkey & Pear Pita Melt\n" +
                "•   1/2 large whole-wheat pita round (save the other half of the pita for a snack on Day 7)\n" +
                "•   3 1/2 oz. low-sodium deli turkey\n" +
                "•   1 medium pear, sliced\n" +
                "•   1 Tbsp. shredded Cheddar cheese\n" +
                "•   1 cup mixed greens\n" +
                "P.M. Snack \n" +
                "•   1 medium plum\n" +
                "•   4 walnuts halves\n" +
                "Dinner \n" +
                "•   1 serving Lemon-Garlic Shrimp over Orzo with Zucchini\n" +
                "•   1 clementine plus 1 serving Chocolate & Nut Butter Bites, to enjoy after dinner\n" +
                "Daily Totals: 1,216 calories, 80 g protein, 162 g carbohydrates, 30 g fiber, 31 g fat, 1,290 mg sodium','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('2','7','Breakfast \n" +
                "Egg Toast with Salsa\n" +
                "•   1 slice whole-wheat bread, toasted\n" +
                "•   1 egg, cooked in 1/4 tsp. olive oil\n" +
                "•   Pinch each of salt and pepper\n" +
                "•   2 Tbsp. Pico de Gallo or salsa\n" +
                "Top bread with egg, salt, pepper and Pico de Gallo.\n" +
                "•   1 medium banana\n" +
                "A.M. Snack \n" +
                "•   1/2 large whole-wheat pita round, toasted\n" +
                "•   2 Tbsp. hummus\n" +
                "Lunch (324 calories)\n" +
                "•   1 1/2 cups Chicken Chili with Sweet Potatoes\n" +
                "P.M. Snack (32 calories)\n" +
                "•   1/2 cup raspberries\n" +
                "Dinner \n" +
                "•   1 1/3 cups Creamy Fettuccine with Brussels Sprouts & Mushrooms\n" +
                "•   1/2-ounce dark chocolate, to enjoy after dinner\n" +
                "Daily Totals: 1,205 calories, 62 g protein, 171 g carbohydrates, 30 g fiber, 36 g fat, 1,754 mg sodium','','0','"+bp_id+"')");

    }

    public  void insert_moderate(String bp_id){
        db = dh.getWritableDatabase();
        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','1','Breakfast \n" +
                "•   1 serving Spinach & Egg Scramble with Raspberries\n" +
                "A.M. Snack \n" +
                "•   1 cup low-fat plain kefir\n" +
                "Lunch \n" +
                "•   1 serving Stuffed Sweet Potato with Hummus Dressing\n" +
                "P.M. Snack \n" +
                "•   ¼ cup unsalted dry-roasted almonds\n" +
                "Dinner \n" +
                "•   1 serving Quinoa Avocado Salad with Buttermilk Dressing','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','2','Breakfast\n" +
                "•   1 serving Peanut Butter & Chia Berry Jam English Muffin\n" +
                "A.M. Snack \n" +
                "•   ¾ cup low-fat plain Greek yogurt\n" +
                "•   ½ cup blueberries\n" +
                "Lunch \n" +
                "•   1 serving Vegan Superfood Grain Bowls\n" +
                "P.M. Snack (259 calories)\n" +
                "•   1 cup edamame (in pods)\n" +
                "•   1 medium peach\n" +
                "Dinner \n" +
                "•   1 serving Greek Stuffed Eggplant\n" +
                "•   1 serving Cucumber, Tomato & Avocado Salad','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','3','Breakfast \n" +
                "•   1 cup low-fat plain Greek yogurt\n" +
                "•   ⅓ cup blackberries\n" +
                "•   3 Tbsp. sliced almonds\n" +
                "A.M. Snack (131 calories)\n" +
                "•   1 large pear\n" +
                "Lunch (381 calories)\n" +
                "•   1 serving Vegan Superfood Grain Bowls\n" +
                "P.M. Snack (262 calories)\n" +
                "•   20 dried walnut halves\n" +
                "Dinner \n" +
                "•   1 serving Easy Pea & Spinach Carbonara','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','4','Breakfast \n" +
                "•   1 serving Peanut Butter & Chia Berry Jam English Muffin\n" +
                "A.M. Snack \n" +
                "•   20 unsalted dry-roasted almonds\n" +
                "Lunch \n" +
                "•   1 serving Vegan Superfood Grain Bowls\n" +
                "P.M. Snack \n" +
                "•   1 cup low-fat plain kefir\n" +
                "Dinner \n" +
                "•   1 serving Massaged Kale Salad with Roasted Sweet Potato & Black Beans','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','5','Breakfast \n" +
                "•   1 cup low-fat plain Greek yogurt\n" +
                "•   ⅓ cup blackberries\n" +
                "•   3 Tbsp. sliced almonds\n" +
                "A.M. Snack medium apple\n" +
                "•   1½ Tbsp. natural peanut butter\n" +
                "Lunch \n" +
                "•   1 serving Vegan Superfood Grain Bowls\n" +
                "P.M. Snack " +
                "•   1 medium peach\n" +
                "Dinner \n" +
                "•   1 serving Cheesy Black Bean & Corn-Stuffed Portobello Mushrooms\n" +
                "•   1 serving Guacamole Chopped Salad','','0','"+bp_id+"')");


        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','6','Breakfast \n" +
                "•   1 serving Peanut Butter & Chia Berry Jam English Muffin\n" +
                "A.M. Snack \n" +
                "•   ¾ cup low-fat plain Greek yogurt\n" +
                "•   ½ cup blueberries\n" +
                "•   10 dried walnut halves\n" +
                "Lunch \n" +
                "•   1 serving Chopped Salad with Chickpeas, Olives & Feta\n" +
                "•   1 medium peach\n" +
                "P.M. Snack (200 calories)\n" +
                "•   1 cup edamame (in pods)\n" +
                "Dinner \n" +
                "•   1 serving Tofu & Watercress Salad with Mango & Avocado\n" +
                "•   1-oz. slice whole-wheat baguette\n" +
                "•   small apple.','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('3','7','Breakfast \n" +
                "•   1 serving Spinach & Egg Scramble with Raspberries\n" +
                "A.M. Snack\n" +
                "•   1 cup low-fat plain kefir\n" +
                "•   1 medium apple\n" +
                "Lunch \n" +
                "•   1 serving Chopped Salad with Chickpeas, Olives & Feta\n" +
                "•   1 medium peach\n" +
                "P.M. Snack \n" +
                "•   30 unsalted dry-roasted almonds\n" +
                "Dinner \n" +
                "•   1 serving Chipotle Tofu Tacos\n" +
                "•   ¼ cup guacamole','','0','"+bp_id+"')");
    }
    public void insert_severre(String bp_id){
        db = dh.getWritableDatabase();
        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','1','Breakfast \n" +
                "Salsa & Egg Toast\n" +
                "• 1 slice whole-wheat bread, toasted\n" +
                "• 1 large egg, cooked in 1/4 tsp. olive oil or coat pan with a thin layer of cooking spray (1-second spray). Season with a pinch each of kosher salt and pepper.\n" +
                "• 2 Tbsp. salsa\n" +
                "Top toast with egg and salsa.\n" +
                "• 1 medium banana\n" +
                "A.M. Snack \n" +
                "• 3/4 cup blueberries\n" +
                "Lunch \n" +
                "White Beans & Veggie Salad\n" +
                "• 2 cups mixed greens\n" +
                "• 3/4 cup veggies of your choice (try cucumbers and tomatoes)\n" +
                "• 1/3 cup white beans, rinsed\n" +
                "• 1/2 avocado, diced\n" +
                "Combine ingredients and top salad with 1 Tbsp. red-wine vinegar, 2 tsp. olive oil and freshly ground pepper.\n" +
                "P.M. Snack \n" +
                "• 1 medium orange\n" +
                "Dinner \n" +
                "•   1 serving Garlic Roasted Salmon & Brussels Sprouts\n" +
                "•   1/2 cup cooked lentils seasoned with a pinch each of kosher salt and pepper','','0','"+bp_id+"')");
        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','2','Breakfast \n" +
                "Strawberry Oatmeal\n" +
                "• 1/2 cup rolled oats, cooked in 1 cup skim milk\n" +
                "• 1/2 cup sliced strawberries\n" +
                "Cook oats and top with strawberries and a pinch of cinnamon.\n" +
                "A.M. Snack \n" +
                "• 2 cups cubed cantaloupe\n" +
                "Lunch \n" +
                "Veggie-Hummus Sandwich\n" +
                "• 2 slices whole-wheat bread\n" +
                "• 3 Tbsp. hummus\n" +
                "• 1/4 avocado, mashed\n" +
                "• 1/4 medium red bell pepper, sliced\n" +
                "• 1/4 cup cucumber slices\n" +
                "• 1 cup mixed greens\n" +
                "Spread each slice of bread with hummus and avocado. Top one slice with vegetables and press the slices together to make a sandwich.\n" +
                "P.M. Snack \n" +
                "• 2 medium carrots\n" +
                "• Dinner \n" +
                "• 1 serving Spaghetti Squash with Roasted Tomatoes, Beans & Almond Pesto\n" +
                "• 1 diagonal slice baguette (1/4 inch thick), preferably whole-wheat, topped with 2 Tbsp. grated Parmesan cheese and toasted\n','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','3','Breakfast \n" +
                "Blueberry & Almond Yogurt Parfait\n" +
                "• 3/4 cup nonfat plain Greek yogurt\n" +
                "• 1/4 cup blueberries\n" +
                "• 1 1/2 Tbsp. slivered almonds\n" +
                "Top yogurt with blueberries and almonds.\n" +
                "• 1 2/3 cups cubed cantaloupe\n" +
                "A.M. Snack \n" +
                "• 2 medium carrots\n" +
                "Lunch \n" +
                "Mixed Greens with Lentils & Sliced Apple\n" +
                "• 1 1/2 cups mixed greens\n" +
                "• 1/2 cup cooked lentils\n" +
                "• 1 apple, sliced\n" +
                "• 1 1/2 Tbsp. crumbled feta cheese\n" +
                "Top greens with lentils, 1/2 of the apple slices and feta. Dress the salad with 1 Tbsp. red-wine vinegar and 2 tsp. olive oil. Serve the remaining apple slices on the side.\n" +
                "P.M. Snack \n" +
                "• 1 medium orange\n" +
                "Dinner \n" +
                "• 1 1/3 cups Roasted Beet Salad\n" +
                "• 4 oz. chicken breast, cooked in 1 tsp. olive oil and seasoned with 1/4 tsp. cumin and a pinch each of kosher salt and pepper','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','4','Breakfast \n" +
                "White Bean & Avocado Toast\n" +
                "• 1 slice whole-wheat bread, toasted\n" +
                "• 1/2 avocado, mashed\n" +
                "• 1/4 cup canned white beans, rinsed and mashed\n" +
                "Top toast with mashed avocado and white beans. Season with a pinch each of kosher salt, pepper and crushed red pepper.\n" +
                "A.M. Snack \n" +
                "• 2 medium carrots\n" +
                "Lunch \n" +
                "Green Salad with Chicken\n" +
                "• 2 cups mixed greens\n" +
                "• 3 oz. leftover cooked chicken breast\n" +
                "• 2/3 cup Roasted Beet Salad\n" +
                "Combine ingredients and top with 2 tsp. each lemon juice and olive oil.\n" +
                "P.M. Snack \n" +
                "• 1 medium orange\n" +
                "Dinner\n" +
                "Black Bean & Corn Tacos\n" +
                "• 2 corn tortillas, warmed\n" +
                "• 1/4 cup canned black beans, rinsed and mashed\n" +
                "• 1/2 cup corn\n" +
                "• 1/2 avocado, diced\n" +
                "• 1/4 cup salsa\n" +
                "Spread tortillas with beans. Top with corn, avocado and salsa.\n" +
                "• 2 cups mixed greens, topped with 1 Tbsp. lime juice, 2 tsp. olive oil and a pinch each of kosher salt and pepper.','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','5','Breakfast \n" +
                "Blueberry & Almond Yogurt Parfait\n" +
                "• 3/4 cup nonfat plain Greek yogurt\n" +
                "• 1/4 cup blueberries\n" +
                "• 1 1/2 Tbsp. slivered almonds\n" +
                "Top yogurt with blueberries and almonds.\n" +
                "• 2 cups cubed cantaloupe\n" +
                "A.M. Snack \n" +
                "• 1/2 bell pepper, sliced\n" +
                "Lunch (336 calories)\n" +
                "Toaster-Oven Tostadas\n" +
                "• 2 corn tortillas\n" +
                "• 1/2 cup canned black beans, rinsed\n" +
                "• 1/2 cup corn\n" +
                "• 1/2 bell pepper, sliced\n" +
                "• 2 Tbsp. shredded Cheddar cheese\n" +
                "Top tortillas with beans, corn, bell pepper and cheese. Toast until the cheese begins to melt.\n" +
                "P.M. Snack \n" +
                "• 1/2 cup blueberries\n" +
                "Dinner \n" +
                "• 2 1/2 cups Avocado & Shrimp Chopped Salad\n" +
                "• 1 diagonal slice baguette (1/4 inch thick), preferably whole-wheat, toasted\n" +
                "Evening Snack (84 calories)\n" +
                "• 2 kiwis','','0','"+bp_id+"')");


        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','6','Breakfast \n" +
                "Banana Oatmeal\n" +
                "• 1/3 cup rolled oats, cooked in 2/3 cup milk\n" +
                "• 1 medium banana, sliced\n" +
                "Cook oats and top with banana and a pinch of cinnamon.\n" +
                "•   A.M. Snack \n" +
                "•   1 cup blueberries\n" +
                "•   1 Tbsp. unsalted dry-roasted almonds\n" +
                "Lunch \n" +
                "Tuna & White Bean Salad\n" +
                "• 1/2 cup canned white beans, rinsed\n" +
                "• 2 1/2 oz. (about 1/4 cup) chunk light tuna in water, drained\n" +
                "• 8 cherry tomatoes, halved\n" +
                "• 1/2 cucumber, sliced\n" +
                "• 1 Tbsp. red-wine vinegar\n" +
                "• 2 tsp. olive oil\n" +
                "• 2 cups mixed greens\n" +
                "Combine beans, tuna, tomatoes and cucumber. Toss with vinegar, oil and a pinch each of kosher salt and pepper. Serve over greens.\n" +
                "P.M. Snack \n" +
                "• 1 medium orange\n" +
                "Dinner \n" +
                "• 1 1/2 cups Chicken Chili with Sweet Potatoes\n" +
                "• 2 cups mixed greens, topped with 1 Tbsp. balsamic vinegar, 2 tsp. olive oil and a pinch each of kosher salt and pepper.','','0','"+bp_id+"')");

        db.execSQL("INSERT INTO " + Sugguestion.this.dh.RECOMMENDATIONS + "( bp , day , description , date_time , status, bp_id) " +
                "VALUES('4','7','Breakfast \n" +
                "Banana Oatmeal\n" +
                "• 1/3 cup rolled oats, cooked in 2/3 cup milk\n" +
                "• 1 medium banana, sliced\n" +
                "Cook oats and top with banana and a pinch of cinnamon.\n" +
                "•   A.M. Snack \n" +
                "•   1 cup blueberries\n" +
                "•   1 Tbsp. unsalted dry-roasted almonds\n" +
                "Lunch \n" +
                "Tuna & White Bean Salad\n" +
                "• 1/2 cup canned white beans, rinsed\n" +
                "• 2 1/2 oz. (about 1/4 cup) chunk light tuna in water, drained\n" +
                "• 8 cherry tomatoes, halved\n" +
                "• 1/2 cucumber, sliced\n" +
                "• 1 Tbsp. red-wine vinegar\n" +
                "• 2 tsp. olive oil\n" +
                "• 2 cups mixed greens\n" +
                "Combine beans, tuna, tomatoes and cucumber. Toss with vinegar, oil and a pinch each of kosher salt and pepper. Serve over greens.\n" +
                "P.M. Snack \n" +
                "• 1 medium orange\n" +
                "Dinner \n" +
                "•   1 1/2 cups Chicken Chili with Sweet Potatoes\n" +
                "•   2 cups mixed greens, topped with 1 Tbsp. balsamic vinegar, 2 tsp. olive oil and a pinch each of kosher salt and pepper.','','0','"+bp_id+"')");

    }
}
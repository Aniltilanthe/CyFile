package at.tugraz.tc.cyfile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import java.util.ArrayList;

import javax.inject.Inject;

import at.tugraz.tc.cyfile.MainActivity;
import at.tugraz.tc.cyfile.R;
import at.tugraz.tc.cyfile.domain.Note;
import at.tugraz.tc.cyfile.note.NoteService;

public class DisplayNoteActivity extends BaseActivity {

    @Inject
    NoteService noteService;

    private Note loadedNote;
    private TextView textContent;
    private TextView textTitle;

    private BoomMenuButton buttonContainer;
    private ArrayList<Pair> piecesAndButtons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_note);

        getActivityComponent().inject(this);

        initView();

        Intent intent = getIntent();
        String noteId = intent.getStringExtra(MainActivity.NOTE_ID);
        loadNote(noteId);

        onOpenNote();
    }

    private void initView() {
        textContent = findViewById(R.id.NOTE_CONTENT);
        textTitle = findViewById(R.id.NOTE_TITLE);

        buttonContainer = (BoomMenuButton) findViewById(R.id.BTN_CONTAINER);
        assert buttonContainer != null;
        buttonContainer.setButtonEnum(ButtonEnum.SimpleCircle);
        buttonContainer.setPiecePlaceEnum(PiecePlaceEnum.DOT_2_2);
        buttonContainer.setButtonPlaceEnum(ButtonPlaceEnum.SC_2_2);

        //Save button
        SimpleCircleButton.Builder builder = new SimpleCircleButton.Builder();
        builder.normalImageRes(R.drawable.ic_save_black_24dp);
        builder.shadowEffect(true);
        builder.rippleEffect(true);
        builder.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                onSelectSaveNote();
            }
        });
        buttonContainer.addBuilder(builder);

        //Delete button
        SimpleCircleButton.Builder builder2 = new SimpleCircleButton.Builder();
        builder2.normalImageRes(R.drawable.ic_delete_black_24dp);
        builder2.shadowEffect(true);
        builder2.rippleEffect(true);
        builder2.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                onSelectDeleteNote();
            }
        });

        buttonContainer.addBuilder(builder2);


    }

    public void loadNote(String noteId) {
        if (noteId == null) {
            loadedNote = new Note();
        } else {
            loadedNote = noteService.findOne(noteId);
        }
    }

    public void onOpenNote() {
        Log.d("Note Id", loadedNote.getId() + " ");
        Log.d("Note Content", loadedNote.getContent() + " ");

        textContent.setText(loadedNote.getContent());
        textTitle.setText(loadedNote.getTitle());
    }

    public void onSelectSaveNote() {
        String noteTitle = textTitle.getText().toString();
        String noteContent = textContent.getText().toString();
        Log.d("onSelectSaveNote", "Title:- " + noteTitle);
        Log.d("onSelectSaveNote", "Content:- " + noteContent);

        loadedNote.setTitle(noteTitle);
        loadedNote.setContent(noteContent);

        loadedNote = noteService.save(loadedNote);
        finish();
    }

    public void onSelectDeleteNote() {
        Intent intent = getIntent();
        String noteId = intent.getStringExtra(MainActivity.NOTE_ID);

        noteService.delete(noteId);

        finish();
    }
}

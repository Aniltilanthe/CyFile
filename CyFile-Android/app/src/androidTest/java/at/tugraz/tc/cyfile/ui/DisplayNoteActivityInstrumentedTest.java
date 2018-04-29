package at.tugraz.tc.cyfile.ui;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import at.tugraz.tc.cyfile.AppModule;
import at.tugraz.tc.cyfile.BaseInstrumentedTest;
import at.tugraz.tc.cyfile.MainActivity;
import at.tugraz.tc.cyfile.R;
import at.tugraz.tc.cyfile.crypto.NoOpCryptoService;
import at.tugraz.tc.cyfile.domain.Note;
import at.tugraz.tc.cyfile.injection.ApplicationComponent;
import at.tugraz.tc.cyfile.injection.DaggerApplicationComponent;
import at.tugraz.tc.cyfile.note.NoteModule;
import at.tugraz.tc.cyfile.note.impl.InMemoryNoteRepository;
import at.tugraz.tc.cyfile.note.impl.SecureNoteService;
import at.tugraz.tc.cyfile.secret.SecretManager;
import at.tugraz.tc.cyfile.secret.SecretModule;
import at.tugraz.tc.cyfile.secret.SecretPrompter;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DisplayNoteActivityInstrumentedTest extends BaseInstrumentedTest {

    @Mock
    private SecretManager secretManager;

    @Rule
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule
            = new ActivityTestRule<>(MainActivity.class, true, false);

    @Rule
    public IntentsTestRule<DisplayNoteActivity> testRule =
            new IntentsTestRule<>(DisplayNoteActivity.class, true, false);

    private List<Note> testNotes =
            Arrays.asList(new Note("1", "name1", "content1")
                    , new Note("2", "name2", "content2"));


    @Before
    public void init() {
        ApplicationComponent applicationComponent
                = DaggerApplicationComponent.builder()
                .noteModule(new NoteModule(new SecureNoteService(new InMemoryNoteRepository(new HashSet<>(testNotes)), new NoOpCryptoService())))
                .secretModule(new SecretModule(secretManager, mock(SecretPrompter.class)))
                .appModule(mock(AppModule.class))
                .build();

        app.setComponent(applicationComponent);

        mainActivityActivityTestRule.launchActivity(new Intent());
    }

    /**
     * Instrumented test, which will execute on an Android device.
     * <p>
     * See [testing documentation](http://d.android.com/tools/testing).
     */
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        assertEquals("at.tugraz.tc.cyfile", appContext.getPackageName());
    }

    @Test
    public void testViewNote() {
        Intent startIntent = new Intent();
        Note editedNote = testNotes.get(0);
        startIntent.putExtra(MainActivity.NOTE_ID, editedNote.getId());

        testRule.launchActivity(startIntent);

        onView(withId(R.id.NOTE_TITLE)).check(matches(withText(editedNote.getTitle())));
        onView(withId(R.id.NOTE_CONTENT)).check(matches(withText(editedNote.getContent())));
        assertTrue(editedNote.getTitle().length() > 0);
    }

    @Test
    public void testDeleteButton() {
        Intent startIntent = new Intent();
        Note note = testNotes.get(0);
        startIntent.putExtra(MainActivity.NOTE_ID, note.getId());

        testRule.launchActivity(startIntent);

        onView(withId(R.id.BTN_DEL))
                .perform(click());

        onView(withText(note.getTitle()))
                .check(doesNotExist());

    }

    @Test
    public void testSaveButton() {
        Intent startIntent = new Intent();
        Note note = testNotes.get(0);
        startIntent.putExtra(MainActivity.NOTE_ID, note.getId());

        testRule.launchActivity(startIntent);

        onView(withId(R.id.NOTE_TITLE))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText("New title"));

        onView(withId(R.id.NOTE_CONTENT))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText("Next text"));

        onView(withId(R.id.BTN_SAVE))
                .perform(click());

        onView(withText("New title"))
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withText("Next text"))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testNotSavedBack() {
        Intent startIntent = new Intent();
        Note note = testNotes.get(0);
        startIntent.putExtra(MainActivity.NOTE_ID, note.getId());

        testRule.launchActivity(startIntent);

        onView(withId(R.id.TEXT_TITLE))
                .perform(ViewActions.clearText())
                .perform(ViewActions.typeText("New title"));

        Espresso.pressBack();

        onView(withText(note.getTitle()))
                .check(matches(isDisplayed()));

    }
}

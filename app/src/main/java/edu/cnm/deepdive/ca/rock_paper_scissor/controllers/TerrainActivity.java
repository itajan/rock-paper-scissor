package edu.cnm.deepdive.ca.rock_paper_scissor.controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import edu.cnm.deepdive.ca.rock_paper_scissor.R;
import edu.cnm.deepdive.ca.rock_paper_scissor.models.Terrain;
import edu.cnm.deepdive.ca.rock_paper_scissor.views.TerrainView;

/**
 * Creates activity class for Rock/Paper/Scissors, cellular automata.
 */
public class TerrainActivity extends AppCompatActivity {
/** Rest duration of overridden run method thread class inside nested activity class.*/
  private static final int RUNNER_THREAD_REST = 40;
  private static final int RUNNER_THREAD_SLEEP = 50;

  private boolean running = false;
  private boolean inForeground = false;
  private Terrain terrain = null;
  private TerrainView terrainView = null;
  private View terrainLayout;
  private Runner runner = null;

  @Override
  protected void onStart() {
    super.onStart();
    setInForeground(true);
  }

  @Override
  protected void onStop() {
    setInForeground(false);
    super.onStop();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_terrain);
    initializeModel();
    initializeUserInterface();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    boolean running = isRunning();
    menu.findItem(R.id.run_item).setVisible(!running);
    menu.findItem(R.id.pause_item).setVisible(running);
    menu.findItem(R.id.reset_item).setEnabled(!running);
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.run_item:
        setRunning(true);
        break;
      case R.id.pause_item:
        setRunning(false);
        break;
      case R.id.reset_item:
        setInForeground(false);
        initializeModel();
        setInForeground(true);
        break;
      default:
        return super.onOptionsItemSelected(item);
    }
    invalidateOptionsMenu();
    return true;

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.options, menu);
    return super.onCreateOptionsMenu(menu);

  }
/** Instantiates a new terrain with a reference name terrain.
 * Calls the initialize() method inherited from Terrain model to create a lattice with
 * randomly assigned instance of Breed.
 */
  private void initializeModel() {
    terrain = new Terrain();
    terrain.initialize();
  }
/** Assign values for terrainView and terrainLayout objects.*/
  private void initializeUserInterface() {
    terrainLayout = findViewById(R.id.terrainLayout);
    terrainView = (TerrainView) findViewById(R.id.terrainView);
  }

  /**
   * Returns the currently specified value of running field.
   * @return Return on or off flag
   */
  private synchronized boolean isRunning() {
    return running;
  }

  /**
   * Sets the value of running to be used by the Runner class to determine whether to run
   * terrain.step() and terrainView.setSource() methods.
   * Allows the View to be updated or not based on the two conditions specified in
   * the while loop inside the Runner class.
   *
   * @param running on or off flag
   */
  private synchronized void setRunning(boolean running) {
    this.running = running;
  }

  /**
   *
   * Returns the currently specified value of inForeground field based whether the app is paused,
   * running or resetting.
   *
   * @return on or off flag.
   */
  private synchronized boolean isInForeground() {
    return inForeground;
  }

  /**
   * Sets the value of inForeground to be used as a flag to determine whether to reset, pause, or
   * run the app.
   *
   * @param inForeground on or off flag.
   */
  private synchronized void setInForeground(boolean inForeground) {
    if (inForeground) {
      this.inForeground = true;
      if (runner == null) {
        runner = new Runner();
        runner.start();
      }
      terrainLayout.post(new Runnable() {
        @Override
        public void run() {
          terrainView.setSource(terrain.getSnapshot());
        }
      });
    } else {
      this.inForeground = false;
      runner = null;
    }
  }

  /**
   * Determines the state of Runner Thread and sets the Thread sleep method to rest or sleep based
   * upon condition.
   * Based upon the isRunning && isInForeground condition.
   *
   */
  private class Runner extends Thread {

    @Override
    public void run() {
      while (isInForeground()) {
        while (isRunning() && isInForeground()) {
          terrain.step();
          terrainView.setSource(terrain.getSnapshot());
          try {
            Thread.sleep(RUNNER_THREAD_REST);
          } catch (InterruptedException ex) {
            // Do nothing
          }
        }
        try {
          Thread.sleep(RUNNER_THREAD_SLEEP);
        } catch (InterruptedException ex) {

        }
      }
    }
  }
}

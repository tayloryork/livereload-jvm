package co.tyec.livereloadservlet;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @see {link http://docs.oracle.com/javase/tutorial/essential/io/notification.html}
 * @author dwayne
 * 
 */
// TODO make a start/stop/join method
public class Watcher implements Runnable {
  private final WatchService        _watcher;
  private final Map<WatchKey, Path> _keys;
  private final Path                _docroot;
  private final AtomicBoolean       _running = new AtomicBoolean(false);

  public LiveReloadWebSocket listener = null;

  public Watcher(Path docroot) throws Exception {
    _docroot = docroot;
    this._watcher = docroot.getFileSystem().newWatchService();
    this._keys = new HashMap<WatchKey, Path>();

    System.out.format("Scanning %s ...\n", _docroot.toAbsolutePath());
    registerAll(_docroot);
    // System.out.println("Done.");
  }

  private void notify(String path) throws Exception {
      LiveReloadWebSocket l = listener;
    if (l != null)
      l.notifyChange(path);
  }

  @SuppressWarnings("unchecked")
  static <T> WatchEvent<T> cast(WatchEvent<?> event) {
    return (WatchEvent<T>) event;
  }

  /**
   * Register the given directory with the WatchService
   */
  private void register(Path dir) throws IOException {
    WatchKey key = dir.register(_watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    _keys.put(key, dir);
  }

  /**
   * Register the given directory, and all its sub-directories, with the WatchService.
   */
  private void registerAll(final Path start) throws IOException {
    // register directory and sub-directories
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
          throws IOException
      {
        register(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  void start() throws Exception {
    if (_running.compareAndSet(false, true)) {
      Thread t = new Thread(this);
      t.setDaemon(true);
      t.start();
    }
  }

  void stop() throws Exception {
    _running.set(false);
    _watcher.close();
  }

  /**
   * Process all events for keys queued to the watcher
   * 
   * @throws Exception
   */
  @Override
  public void run() {
    try {
      while (_running.get()) {

        // wait for key to be signalled
        WatchKey key = _watcher.take();

        Path dir = _keys.get(key);
        if (dir == null) {
          System.err.println("WatchKey not recognized!!");
          continue;
        }

        for (WatchEvent<?> event : key.pollEvents()) {
          WatchEvent.Kind<?> kind = event.kind();

          // TBD - provide example of how OVERFLOW event is handled
          if (kind == OVERFLOW) {
            continue;
          }

          // Context for directory entry event is the file name of entry
          WatchEvent<Path> ev = cast(event);
          Path name = ev.context();
          Path child = dir.resolve(name);

          // System.out.format("%s: %s ++ %s\n", event.kind().name(), name, _docroot.relativize(child));
          if (kind == ENTRY_MODIFY) {
            notify(_docroot.relativize(child).toString());
          } else if (kind == ENTRY_CREATE) {
            // if directory is created, and watching recursively, then
            // register it and its sub-directories
            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
              registerAll(child);
            }
          }
        }

        // reset key and remove from set if directory no longer accessible
        boolean valid = key.reset();
        if (!valid) {
          _keys.remove(key);

          // all directories are inaccessible
          if (_keys.isEmpty()) {
            break;
          }
        }
      }
    } catch (InterruptedException | ClosedWatchServiceException exc) {
      // stop
    } catch (Exception exc) {
      exc.printStackTrace();
    } finally {
      _running.set(false);
    }
  }
}

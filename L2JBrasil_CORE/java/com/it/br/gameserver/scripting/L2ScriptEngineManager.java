/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.it.br.gameserver.scripting;

import com.it.br.Config;
import com.it.br.configuration.settings.ServerSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.it.br.configuration.Configurator.getSettings;


/**
 * Caches script engines and provides funcionality for executing and managing scripts.<BR>
 *
 * @author KenM
 */
public final class L2ScriptEngineManager {
    private static final Logger _log = LoggerFactory.getLogger(L2ScriptEngineManager.class);
    public final static File SCRIPT_FOLDER = new File(getSettings(
            ServerSettings.class).getDatapackDirectory().getAbsolutePath(), "data/jscript");

    public static L2ScriptEngineManager getInstance() {
        return SingletonHolder._instance;
    }

    private final Map<String, ScriptEngine> _nameEngines = new HashMap<>();
    private final Map<String, ScriptEngine> _extEngines = new HashMap<>();
    private final List<ScriptManager<?>> _scriptManagers = new LinkedList<>();
    private final CompiledScriptCache _cache;
    private File _currentLoadingScript;

    /**
     * Informs(logs) the scripts being loaded.<BR>
     * Apply only when executing script from files.<BR>
     */
    private final boolean VERBOSE_LOADING = Config.VERBOSE_LOADING;
    /**
     * If the script engine supports compilation the script is compiled before execution.<BR>
     */
    private final boolean ATTEMPT_COMPILATION = Config.ATTEMPT_COMPILATION;
    /**
     * Use Compiled Scripts Cache.<BR>
     * Only works if ATTEMPT_COMPILATION is true.<BR>
     * DISABLED DUE ISSUES (if a superclass file changes subclasses are not recompiled where they should)
     */
    private final boolean USE_COMPILED_CACHE = Config.USE_COMPILED_CACHE;
    /**
     * Clean an previous error log(if such exists) for the script being loaded before trying to load.<BR>
     * Apply only when executing script from files.<BR>
     */
    private final boolean PURGE_ERROR_LOG = Config.PURGE_ERROR_LOG;

    private L2ScriptEngineManager() {
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        List<ScriptEngineFactory> factories = scriptEngineManager.getEngineFactories();
        if (USE_COMPILED_CACHE)
            _cache = this.loadCompiledScriptCache();
        else
            _cache = null;
        _log.info("Initializing Script Engine Manager");
        for (ScriptEngineFactory factory : factories)
            try {
                ScriptEngine engine = factory.getScriptEngine();
                boolean reg = false;
                for (String name : factory.getNames()) {
                    ScriptEngine existentEngine = _nameEngines.get(name);
                    if (existentEngine != null) {
                        double engineVer = Double.parseDouble(factory.getEngineVersion());
                        double existentEngVer = Double.parseDouble(existentEngine.getFactory().getEngineVersion());
                        if (engineVer <= existentEngVer)
                            continue;
                    }
                    reg = true;
                    _nameEngines.put(name, engine);
                }
                if (reg)
                    _log.info("Script Engine: {} {} - Language: {} - Language Version: {}", factory.getEngineName(),
                            factory.getEngineVersion(), factory.getLanguageName(),  factory.getLanguageVersion());
                for (String ext : factory.getExtensions())
                    if (!ext.equals("java") || factory.getLanguageName().equals("java"))
                        _extEngines.put(ext, engine);
            } catch (Exception e) {
                _log.warn("Failed initializing factory.", e);
            }
        this.preConfigure();
    }

    private void preConfigure() {
        // java class path
        // Jython sys.path
        String dataPackDirForwardSlashes = SCRIPT_FOLDER.getPath().replaceAll("\\\\", "/");
        String configScript = "import sys;sys.path.insert(0,'" + dataPackDirForwardSlashes + "');";
        try {
            this.eval("jython", configScript);
        } catch (ScriptException e) {
            _log.error("Failed preconfiguring jython: {}", e.getMessage());
        }
    }

    private ScriptEngine getEngineByName(String name) {
        return _nameEngines.get(name);
    }

    private ScriptEngine getEngineByExtension(String ext) {
        return _extEngines.get(ext);
    }

    public void executeScriptList(File list) throws IOException {
        if (list.isFile()) {
            LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(list)));
            String line;
            File file;
            while ((line = lnr.readLine()) != null) {
                String[] parts = line.trim().split("#");
                if (parts.length > 0 && !parts[0].startsWith("#") && parts[0].length() > 0) {
                    line = parts[0];
                    if (line.endsWith("/**"))
                        line = line.substring(0, line.length() - 3);
                    else if (line.endsWith("/*"))
                        line = line.substring(0, line.length() - 2);
                    file = new File(SCRIPT_FOLDER, line);
                    if (file.isDirectory() && parts[0].endsWith("/**"))
                        this.executeAllScriptsInDirectory(file, true, 32);
                    else if (file.isDirectory() && parts[0].endsWith("/*"))
                        this.executeAllScriptsInDirectory(file);
                    else if (file.isFile())
                        try {
                            this.executeScript(file);
                        } catch (ScriptException e) {
                            this.reportScriptFileError(file, e);
                        }
                    else
                        _log.warn("Failed loading: ({}) @ {}:{} - Reason: doesn't exists or isn't a file.", file.getCanonicalPath(),
                                list.getName(), lnr.getLineNumber());
                }
            }
            lnr.close();
        } else
            throw new IllegalArgumentException("Argument must be an file containing a list of scripts to be loaded");
    }

    public void executeAllScriptsInDirectory(File dir) {
        this.executeAllScriptsInDirectory(dir, false, 0);
    }

    public void executeAllScriptsInDirectory(File dir, boolean recurseDown, int maxDepth) {
        this.executeAllScriptsInDirectory(dir, recurseDown, maxDepth, 0);
    }

    private void executeAllScriptsInDirectory(File dir, boolean recurseDown, int maxDepth, int currentDepth) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles())
                if (file.isDirectory() && recurseDown && maxDepth > currentDepth) {
                    if (VERBOSE_LOADING)
                        _log.debug("Entering folder: {}", file.getName() );
                    this.executeAllScriptsInDirectory(file, recurseDown, maxDepth, currentDepth + 1);
                } else if (file.isFile())
                    try {
                        String name = file.getName();
                        int lastIndex = name.lastIndexOf('.');
                        String extension;
                        if (lastIndex != -1) {
                            extension = name.substring(lastIndex + 1);
                            ScriptEngine engine = this.getEngineByExtension(extension);
                            if (engine != null)
                                this.executeScript(engine, file);
                        }
                    } catch (FileNotFoundException e) {
                        _log.error(e.getMessage(), e);
                    } catch (ScriptException e) {
                        this.reportScriptFileError(file, e);
                    }
        } else
            throw new IllegalArgumentException("The argument directory either doesnt exists or is not an directory.");
    }

    public CompiledScriptCache getCompiledScriptCache() throws IOException {
        return _cache;
    }

    public CompiledScriptCache loadCompiledScriptCache() {
        if (USE_COMPILED_CACHE) {
            File file = new File(SCRIPT_FOLDER, "CompiledScripts.cache");
            if (file.isFile()) {
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(new FileInputStream(file));
                    CompiledScriptCache cache = (CompiledScriptCache) ois.readObject();
                    return cache;
                } catch (InvalidClassException e) {
                    _log.error("Failed loading Compiled Scripts Cache, invalid class (Possibly outdated).", e);
                } catch (IOException e) {
                    _log.error("Failed loading Compiled Scripts Cache from file.", e);
                } catch (ClassNotFoundException e) {
                    _log.error("Failed loading Compiled Scripts Cache, class not found.", e);
                } finally {
                    try {
                        ois.close();
                    } catch (Exception e) {
                    }
                }
                return new CompiledScriptCache();
            } else
                return new CompiledScriptCache();
        }
        return null;
    }

    public void executeScript(File file) throws ScriptException, FileNotFoundException {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        String extension;
        if (lastIndex != -1)
            extension = name.substring(lastIndex + 1);
        else
            throw new ScriptException("Script file (" + name + ") doesnt has an extension that identifies the ScriptEngine to be used.");
        ScriptEngine engine = this.getEngineByExtension(extension);
        if (engine == null)
            throw new ScriptException("No engine registered for extension (" + extension + ")");
        else
            this.executeScript(engine, file);
    }

    public void executeScript(String engineName, File file) throws FileNotFoundException, ScriptException {
        ScriptEngine engine = this.getEngineByName(engineName);
        if (engine == null)
            throw new ScriptException("No engine registered with name (" + engineName + ")");
        else
            this.executeScript(engine, file);
    }

    public void executeScript(ScriptEngine engine, File file) throws FileNotFoundException, ScriptException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        if (VERBOSE_LOADING)
            _log.info("Loading Script: {}", file.getAbsolutePath());
        if (PURGE_ERROR_LOG) {
            String name = file.getAbsolutePath() + ".error.log";
            File errorLog = new File(name);
            if (errorLog.isFile())
                errorLog.delete();
        }
        if (engine instanceof Compilable && ATTEMPT_COMPILATION) {
            ScriptContext context = new SimpleScriptContext();
            context.setAttribute("mainClass", getClassForFile(file).replace('/', '.').replace('\\', '.'), ScriptContext.ENGINE_SCOPE);
            context.setAttribute(ScriptEngine.FILENAME, file.getName(), ScriptContext.ENGINE_SCOPE);
            context.setAttribute("classpath", SCRIPT_FOLDER.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
            context.setAttribute("sourcepath", SCRIPT_FOLDER.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
            // context.setAttribute(JythonScriptEngine.JYTHON_ENGINE_INSTANCE, engine, ScriptContext.ENGINE_SCOPE);
            this.setCurrentLoadingScript(file);
            ScriptContext ctx = engine.getContext();
            try {
                engine.setContext(context);
                if (USE_COMPILED_CACHE) {
                    CompiledScript cs = _cache.loadCompiledScript(engine, file);
                    cs.eval(context);
                } else {
                    Compilable eng = (Compilable) engine;
                    CompiledScript cs = eng.compile(reader);
                    cs.eval(context);
                }
            } finally {
                engine.setContext(ctx);
                this.setCurrentLoadingScript(null);
                context.removeAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE);
                context.removeAttribute("mainClass", ScriptContext.ENGINE_SCOPE);
            }
        } else {
            ScriptContext context = new SimpleScriptContext();
            context.setAttribute("mainClass", getClassForFile(file).replace('/', '.').replace('\\', '.'), ScriptContext.ENGINE_SCOPE);
            context.setAttribute(ScriptEngine.FILENAME, file.getName(), ScriptContext.ENGINE_SCOPE);
            context.setAttribute("classpath", SCRIPT_FOLDER.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
            context.setAttribute("sourcepath", SCRIPT_FOLDER.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
            this.setCurrentLoadingScript(file);
            try {
                engine.eval(reader, context);
            } finally {
                this.setCurrentLoadingScript(null);
                engine.getContext().removeAttribute(ScriptEngine.FILENAME, ScriptContext.ENGINE_SCOPE);
                engine.getContext().removeAttribute("mainClass", ScriptContext.ENGINE_SCOPE);
            }
        }
    }

    public static String getClassForFile(File script) {
        String path = script.getAbsolutePath();
        String scpPath = SCRIPT_FOLDER.getAbsolutePath();
        if (path.startsWith(scpPath)) {
            int idx = path.lastIndexOf('.');
            return path.substring(scpPath.length() + 1, idx);
        }
        return null;
    }

    public ScriptContext getScriptContext(ScriptEngine engine) {
        return engine.getContext();
    }

    public ScriptContext getScriptContext(String engineName) {
        ScriptEngine engine = this.getEngineByName(engineName);
        if (engine == null)
            throw new IllegalStateException("No engine registered with name (" + engineName + ")");
        else
            return this.getScriptContext(engine);
    }

    public Object eval(ScriptEngine engine, String script, ScriptContext context) throws ScriptException {
        if (engine instanceof Compilable && ATTEMPT_COMPILATION) {
            Compilable eng = (Compilable) engine;
            CompiledScript cs = eng.compile(script);
            return context != null ? cs.eval(context) : cs.eval();
        } else
            return context != null ? engine.eval(script, context) : engine.eval(script);
    }

    public Object eval(String engineName, String script) throws ScriptException {
        return this.eval(engineName, script, null);
    }

    public Object eval(String engineName, String script, ScriptContext context) throws ScriptException {
        ScriptEngine engine = this.getEngineByName(engineName);
        if (engine == null)
            throw new ScriptException("No engine registered with name (" + engineName + ")");
        else
            return this.eval(engine, script, context);
    }

    public Object eval(ScriptEngine engine, String script) throws ScriptException {
        return this.eval(engine, script, null);
    }

    public void reportScriptFileError(File script, ScriptException e) {
        String dir = script.getParent();
        String name = script.getName() + ".error.log";
        if (dir != null) {
            File file = new File(dir + "/" + name);
            FileOutputStream fos = null;
            try {
                if (!file.exists())
                    file.createNewFile();
                fos = new FileOutputStream(file);
                String errorHeader = "Error on: " + file.getCanonicalPath() + "\r\nLine: " + e.getLineNumber() + " - Column: " + e.getColumnNumber() + "\r\n\r\n";
                fos.write(errorHeader.getBytes());
                fos.write(e.getMessage().getBytes());
                _log.warn("Failed executing script: {}. See {} for details.", script.getAbsolutePath(), file.getName());
            } catch (IOException ioe) {
                _log.warn("Failed executing script: {}\r\n{} Additionally failed when trying to write an error report on script directory. Reason: {} ",
                        script.getAbsolutePath(), e.getMessage(), ioe.getMessage());
                _log.warn(e.getMessage(), e);
            } finally {
                try {
                    fos.close();
                } catch (Exception e1) {
                }
            }
        } else
            _log.warn("Failed executing script: {}\r\n{} Additionally failed when trying to write an error report on script directory.",
                    script.getAbsolutePath(), e.getMessage());
    }

    public void registerScriptManager(ScriptManager<?> manager) {
        _scriptManagers.add(manager);
    }

    public void removeScriptManager(ScriptManager<?> manager) {
        _scriptManagers.remove(manager);
    }

    public List<ScriptManager<?>> getScriptManagers() {
        return _scriptManagers;
    }

    /**
     * @param currentLoadingScript The currentLoadingScript to set.
     */
    protected void setCurrentLoadingScript(File currentLoadingScript) {
        _currentLoadingScript = currentLoadingScript;
    }

    /**
     * @return Returns the currentLoadingScript.
     */
    protected File getCurrentLoadingScript() {
        return _currentLoadingScript;
    }

    @SuppressWarnings("synthetic-access")
    private static class SingletonHolder {
        protected static final L2ScriptEngineManager _instance = new L2ScriptEngineManager();
    }
}

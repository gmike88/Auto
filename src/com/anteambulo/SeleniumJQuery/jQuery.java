package com.anteambulo.SeleniumJQuery;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.openqa.selenium.WebElement;

public class jQuery implements Iterable<WebElement> {
  public static abstract class Eacher {
    public abstract void invoke(Integer index, WebElement ele);
  }

  public static abstract class Mapper<T> {
    public abstract T invoke(Integer index, WebElement ele);
  }

  public static class Position {
    public final Long top;
    public final Long left;

    public Position(Long top, Long left) {
      this.top = top;
      this.left = left;
    }
  }

  public static abstract class Sorter {
    public abstract int invoke(WebElement a, WebElement b);
  }

  public static String createRef(long ref) {
    return "window.SeleniumjQuery_" + ref;
  }

  private String selector;
  private Long length;
  private final String ref;
  private final jQueryFactory jqf;

  private boolean closed = false;

  private final jQuery parent;

  private final List<jQuery> children = new ArrayList<jQuery>();

  private long timeout = 0;

  private long startTime = 0;

  private int html_hash = -1;

  public jQuery(jQueryFactory jqf, long id, String query) {
    this(jqf, createRef(id), query, null);
  }

  public jQuery(jQueryFactory jqf, long id, WebElement we) {
    this(jqf, createRef(id), null, null);
    init(we);
  }

  public jQuery(jQueryFactory jqf, String reference, jQuery parent) {
    this.jqf = jqf;
    this.ref = reference;
    this.parent = parent;
  }

  public jQuery(jQueryFactory jqf, String reference, String query) {
    this(jqf, reference, query, null);
  }

  public jQuery(jQueryFactory jqf, String reference, String query, jQuery parent) {
    this(jqf, reference, parent);

    if (parent != null) {
      this.selector = parent.selector + query;
    } else {
      this.selector = query;
    }

    if (query != null && parent == null) {
      init();
    }
  }

  /**
   * Returns a NEW set that contains both the original and newly selected
   * elements
   * 
   * @return
   */
  public jQuery add(jQuery jq) {
    return subset(".add(" + jq.ref + ");");
  }

  /**
   * Returns a NEW set that contains both the original and newly selected
   * elements
   * 
   * @return
   */
  public jQuery add(String selector_or_html) {
    return subset(".add(arguments[0]);", selector_or_html);
  }

  public jQuery addClass(String cls) {
    jsref(".addClass(arguments[0]);", cls);
    return this;
  }

  public jQuery after(jQuery jq) {
    jsref(".after(" + jq.ref + ")");
    return this;
  }

  public jQuery after(String selector_or_html) {
    jsref(".after(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery after(WebElement we) {
    jsref(".after(arguments[0])", we);
    return this;
  }

  public jQuery andSelf() {
    return subset(".andSelf()");
  }

  public jQuery append(jQuery jq) {
    jsref(".append(" + jq.ref + ")");
    return this;
  }

  public jQuery append(String selector_or_html) {
    jsref(".append(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery append(WebElement we) {
    jsref(".append(arguments[0])", we);
    return this;
  }

  public jQuery appendTo(jQuery jq) {
    return subset(".appendTo(" + jq.ref + ")");
  }

  public jQuery appendTo(String selector_or_html) {
    return subset(".appendTo(arguments[0])", selector_or_html);
  }

  public jQuery appendTo(WebElement we) {
    return subset(".appendTo(arguments[0])", we);
  }

  public String attr(String key) {
    return String.valueOf(jsret(".attr(arguments[0]);", key));
  }

  public jQuery attr(String key, String value) {
    jsref(".attr(arguments[0],arguments[1]);", key, value);
    return this;
  }

  public jQuery before(jQuery jq) {
    jsref(".before(" + jq.ref + ")");
    return this;
  }

  public jQuery before(String selector_or_html) {
    jsref(".before(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery before(WebElement we) {
    jsref(".before(arguments[0])", we);
    return this;
  }

  public jQuery blur() {
    jsref(".blur();");
    return this;
  }

  public jQuery change() {
    jsref(".change();");
    return this;
  }

  private void check() {
    if (closed) {
      throw new IllegalStateException("Cannot use a closed jquery object.");
    }
  }

  public jQuery children() {
    return subset(".children()");
  }

  public jQuery children(String selector) {
    return subset(".children(arguments[0])", selector);
  }

  public jQuery clear() {
    return val("");
  }

  public jQuery clearQueue() {
    jsref(".clearQueue('fx');");
    return this;
  }

  public jQuery clearQueue(String queue_name) {
    jsref(".clearQueue(arguments[0]);", queue_name);
    return this;
  }

  public jQuery clearTimeout() {
    this.timeout = 0;
    return this;
  }

  public jQuery click() {
    jsref(".click();");
    return this;
  }

  @Override
  public jQuery clone() {
    return subset(".clone()");
  }

  public jQuery clone(boolean with_data_and_events) {
    return subset(".clone(arguments[0])", with_data_and_events);
  }

  public jQuery clone(boolean with_data_and_events, boolean deep) {
    return subset(".clone(arguments[0],arguments[1])", with_data_and_events, deep);
  }

  public void close() {
    if (!closed) {
      closed = true;
      for (jQuery child : children) {
        child.close();
      }
    }
  }

  public jQuery closest(String selector) {
    return subset(".closest(arguments[0])", selector);
  }

  public int combinedAttrHash(String attr) {
    return ((String) js("var v = '';" +
      "var attr = arguments[0];" +
      ref + ".each(function(){v+='|'+" + jqf.getRef() + "(this).attr(attr);});" +
      "return v", attr)).hashCode();
  }

  public int combinedMethodHash(String meth) {
    return ((String) js("var v = '';" +
      "var meth = arguments[0];" +
      ref + ".each(function(){v+='|'+" + jqf.getRef() + "(this)[meth]();});" +
      "return v", meth)).hashCode();
  }

  public jQuery contents() {
    return subset(".contents()");
  }

  public String createRef() {
    return createRef(jqf.createId());
  }

  public String css(String key) {
    return (String) jsret(".css(arguments[0])", key);
  }

  public jQuery css(String key, String val) {
    jsref(".css(arguments[0],arguments[1])", key, val);
    return this;
  }

  public Object data(String key) {
    return jsret(".data(arguments[0]);", key);
  }

  public jQuery data(String key, Object value) {
    jsref(".data(arguments[0],arguments[1]);", key, value);
    return this;
  }

  public jQuery dblclick() {
    jsref(".dblclick();");
    return this;
  }

  public jQuery delay(long millis) {
    jsref(".delay(arguments[0],'fx');", millis);
    return this;
  }

  public jQuery delay(long millis, String queue_name) {
    jsref(".delay(arguments[0],arguments[1]);", millis, queue_name);
    return this;
  }

  public jQuery dequeue() {
    jsref(".dequeue();");
    return this;
  }

  public jQuery detach() {
    jsref(".detatch()");
    return this;
  }

  public jQuery detach(String selector) {
    jsref(".detatch(arguments[0])", selector);
    return this;
  }

  public jQuery die() {
    jsref(".die();");
    return this;
  }

  public jQuery die(String event) {
    jsref(".die(arguments[0]);", event);
    return this;
  }

  public jQuery disable() {
    attr("disabled", "disabled");
    return this;
  }

  public Iterable<jQuery> each() {
    return new Iterable<jQuery>() {
      @Override
      public Iterator<jQuery> iterator() {
        return new Iterator<jQuery>() {
          int i = 0;

          @Override
          public boolean hasNext() {
            return length > i;
          }

          @Override
          public jQuery next() {
            return new jQuery(jqf, jqf.createId(), get(i++));
          }

          @Override
          public void remove() {
          }
        };
      }
    };
  }

  public jQuery each(Eacher inv) {
    for (int i = 0; i < length; i++) {
      inv.invoke(i, get(i));
    }
    return this;
  }

  public jQuery empty() {
    jsref(".empty()");
    return this;
  }

  public jQuery enable() {
    removeAttr("disabled");
    return this;
  }

  public jQuery end() {
    close();
    return this.parent;
  }

  public jQuery eq(int index) {
    return slice(index, index + 1);
  }

  public jQuery error() {
    jsref(".error();");
    return this;
  }

  public jQuery fadeIn() {
    jsref(".fadeIn()");
    return this;
  }

  public jQuery fadeOut() {
    jsref(".fadeOut()");
    return this;
  }

  public jQuery fadeToggle() {
    jsref(".fadeToggle()");
    return this;
  }

  public jQuery filter(String selector) {
    return subset(".not(arguments[0])", selector);
  }

  public jQuery find(String selector) {
    return subset(".find(arguments[0]);", selector);
  }

  public jQuery first() {
    return slice(0, 1);
  }

  public jQuery focus() {
    jsref(".focus();");
    return this;
  }

  public jQuery focusin() {
    jsref(".focusin();");
    return this;
  }

  public jQuery focusout() {
    jsref(".focusout();");
    return this;
  }

  public WebElement get() {
    return get(0);
  }

  public WebElement get(int index) {
    return (WebElement) jsret(".get(" + index + ")");
  }

  public Boolean has(String selector) {
    return (Boolean) jsret(".has(arguments[0]);", selector);
  }

  public jQuery hasClass(String cls) {
    jsref(".hasClass(arguments[0]);", cls);
    return this;
  }

  public Long height() {
    return (Long) jsret(".height()");
  }

  public jQuery height(int height) {
    jsref(".height(arguments[0])", height);
    return this;
  }

  public jQuery hide() {
    jsref(".hide()");
    return this;
  }

  public String html() {
    return (String) jsret(".html();");
  }

  public jQuery html(String html) {
    jsref(".html(arguments[0]);", html);
    return this;
  }

  /**
   * The return value is an integer indicating the position of the first element
   * within the jQuery object relative to its sibling elements.
   * 
   * @see http://api.jquery.com/index/
   * @return
   */
  public Long index() {
    return (Long) jsret(".index();");
  }

  /**
   * Returns an integer indicating the position of the original element relative
   * to the elements matched by the selector. If the element is not found,
   * .index() will return -1.
   * 
   * @see http://api.jquery.com/index/
   * @return
   */
  public Long index(String selector) {
    return (Long) jsret(".index(arguments[0]);", selector);
  }

  /**
   * Returns an integer indicating the position of the passed element relative
   * to the original collection.
   * 
   * @see http://api.jquery.com/index/
   * @return
   */
  public Long index(WebElement we) {
    return (Long) jsret(".index(arguments[0]);", we);
  }

  public jQuery init() {
    if (!closed) {
      close();
    }
    closed = false;
    jsref(" = " + jqf.getRef() + "(arguments[0]);", selector);
    refresh();
    return this;
  }

  public jQuery init(WebElement we) {
    if (!closed) {
      close();
    }
    closed = false;
    jsref("= " + jqf.getRef() + "(arguments[0]);", we);
    refresh();
    return this;
  }

  public Long innerHeight() {
    return (Long) jsret(".innerHeight()");
  }

  public Long innerWidth() {
    return (Long) jsret(".innerWidth()");
  }

  public jQuery insertAfter(jQuery jq) {
    return subset(".insertAfter(" + jq.ref + ")");
  }

  public jQuery insertAfter(String selector_or_html) {
    return subset(".insertAfter(arguments[0])", selector_or_html);
  }

  public jQuery insertAfter(WebElement we) {
    return subset(".insertAfter(arguments[0])", we);
  }

  public jQuery insertBefore(jQuery jq) {
    return subset(".insertBefore(" + jq.ref + ")");
  }

  public jQuery insertBefore(String selector_or_html) {
    return subset(".insertBefore(arguments[0])", selector_or_html);
  }

  public jQuery insertBefore(WebElement we) {
    return subset(".insertBefore(arguments[0])", we);
  }

  public Boolean is(jQuery jq) {
    return (Boolean) jsret(".is(arguments[0]);", jq.ref);
  }

  public boolean is(String selector) {
    return (Boolean) jsret(".is(arguments[0]);", selector);
  }

  public Boolean is(WebElement we) {
    return (Boolean) jsret(".is(arguments[0]);", we);
  }

  public boolean isTimeout() {
    if (timeout == 0) {
      throw new IllegalArgumentException("Cannot tell if timed out -- no timeout set");
    }
    return System.currentTimeMillis() > startTime + timeout;
  }

  @Override
  public Iterator<WebElement> iterator() {
    return new Iterator<WebElement>() {
      int i = 0;

      @Override
      public boolean hasNext() {
        return i < length;
      }

      @Override
      public WebElement next() {
        return get(i++);
      }

      @Override
      public void remove() {
        throw new IllegalAccessError("Not implemented");
      }
    };
  }

  public Object js(String script, Object... args) {
    check();
    return jqf.js(script, args);
  }

  public void jsref(String method, Object... args) {
    js(ref + method, args);
  }

  public Object jsret(String method, Object... args) {
    return js("return " + ref + method, args);
  }

  public jQuery keydown() {
    jsref(".keydown();");
    return this;
  }

  public jQuery keypress() {
    jsref(".keypress();");
    return this;
  }

  public jQuery keyup() {
    jsref(".keyup();");
    return this;
  }

  public jQuery last() {
    return slice(-1);
  }

  public Long length() {
    return length;
  }

  public jQuery load() {
    jsref(".load();");
    return this;
  }

  public <T> List<T> map(Mapper<T> mapper) {
    ArrayList<T> lst = new ArrayList<T>();
    for (int i = 0; i < length; i++) {
      lst.add(mapper.invoke(i, get(i)));
    }
    return lst;
  }

  public jQuery mark() {
    html_hash = combinedMethodHash("html");
    return this;
  }

  public jQuery mousedown() {
    jsref(".mousedown();");
    return this;
  }

  public jQuery mouseenter() {
    jsref(".mouseenter();");
    return this;
  }

  public jQuery mouseleave() {
    jsref(".mouseleave();");
    return this;
  }

  public jQuery mousemove() {
    jsref(".mousemove();");
    return this;
  }

  public jQuery mouseout() {
    jsref(".mouseout();");
    return this;
  }

  public jQuery mouseover() {
    jsref(".mouseover();");
    return this;
  }

  public jQuery mouseup() {
    jsref(".mouseup();");
    return this;
  }

  public jQuery next() {
    return subset(".next()");
  }

  public jQuery next(String filter) {
    return subset(".next(arguments[0])", filter);
  }

  public jQuery nextAll() {
    return subset(".nextAll()");
  }

  public jQuery nextAll(String filter) {
    return subset(".nextAll(arguments[0])", filter);
  }

  public jQuery nextUntil(String selector) {
    return subset(".nextUntil(arguments[0])", selector);
  }

  public jQuery nextUntil(String selector, String filter) {
    return subset(".nextUntil(arguments[0],arguments[1])", selector, filter);
  }

  public jQuery nextUntil(WebElement we) {
    return subset(".nextUntil(arguments[0])", we);
  }

  public jQuery nextUntil(WebElement we, String filter) {
    return subset(".nextUntil(arguments[0],arguments[1])", we, filter);
  }

  public jQuery not(String selector) {
    return subset(".not(arguments[0])", selector);
  }

  public Position offset() {
    @SuppressWarnings("unchecked")
    List<Long> offset = (List<Long>) js("var offset = " + ref + ".offset();" +
      "return [offset.top,offset.left];");
    return new Position(offset.get(0), offset.get(1));
  }

  public Position offsetParent() {
    @SuppressWarnings("unchecked")
    List<Long> offset = (List<Long>) js("var offset = " + ref + ".offsetParent();" +
      "return [offset.top,offset.left];");
    return new Position(offset.get(0), offset.get(1));
  }

  public Long outerHeight() {
    return (Long) jsret(".outerHeight()");
  }

  public Long outerWidth() {
    return (Long) jsret(".outerWidth()");
  }

  public jQuery parent() {
    return subset(".parent()");
  }

  public jQuery parent(String selector) {
    return subset(".parent(arguments[0])", selector);
  }

  public jQuery parents() {
    return subset(".parents()");
  }

  public jQuery parents(String selector) {
    return subset(".parents(arguments[0])", selector);
  }

  public jQuery parentsUntil(String selector) {
    return subset(".parentsUntil(arguments[0])", selector);
  }

  public jQuery parentsUntil(String selector, String filter) {
    return subset(".parentsUntil(arguments[0],arguments[1])", selector, filter);
  }

  public jQuery parentsUntil(WebElement we) {
    return subset(".parentsUntil(arguments[0])", we);
  }

  public jQuery parentsUntil(WebElement we, String filter) {
    return subset(".parentsUntil(arguments[0],arguments[1])", we, filter);
  }

  public jQuery pause() throws TimeoutException {
    return pause(toString());
  }

  public jQuery pause(String msg) throws TimeoutException {
    try {
      if (isTimeout()) {
        long timeout_old = this.timeout;
        clearTimeout();
        throw new TimeoutException("Timeout " + toString() + " after " + timeout_old + " ms:" + msg);
      }
      Thread.sleep(200);
    } catch (InterruptedException e) {
      clearTimeout();
      throw new TimeoutException(msg);
    }
    return this;
  }

  public Position position() {
    @SuppressWarnings("unchecked")
    List<Long> position = (List<Long>) js("var position = " + ref + ".position();" +
      "return [position.top,position.left];");
    return new Position(position.get(0), position.get(1));
  }

  public jQuery prepend(jQuery jq) {
    jsref(".prepend(" + jq.ref + ")");
    return this;
  }

  public jQuery prepend(String selector_or_html) {
    jsref(".prepend(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery prepend(WebElement we) {
    jsref(".prepend(arguments[0])", we);
    return this;
  }

  public jQuery prependTo(jQuery jq) {
    return subset(".prependTo(" + jq.ref + ")");
  }

  public jQuery prependTo(String selector_or_html) {
    return subset(".prependTo(arguments[0])", selector_or_html);
  }

  public jQuery prependTo(WebElement we) {
    return subset(".prependTo(arguments[0])", we);
  }

  public jQuery prev() {
    return subset(".prev()");
  }

  public jQuery prev(String filter) {
    return subset(".prev(arguments[0])", filter);
  }

  public jQuery prevAll() {
    return subset(".prev()");
  }

  public jQuery prevAll(String filter) {
    return subset(".prevAll(arguments[0])", filter);
  }

  public jQuery prevUntil(String selector) {
    return subset(".prevUntil(arguments[0])", selector);
  }

  public jQuery prevUntil(String selector, String filter) {
    return subset(".prevUntil(arguments[0],arguments[1])", selector, filter);
  }

  public jQuery prevUntil(WebElement we) {
    return subset(".prevUntil(arguments[0])", we);
  }

  public jQuery prevUntil(WebElement we, String filter) {
    return subset(".prevUntil(arguments[0],arguments[1])", we, filter);
  }

  public jQuery push(WebElement ele) {
    return pushStack(ele);
  }

  public jQuery pushStack(WebElement ele) {
    jsref(".pushStack(arguments[0])", ele);
    refresh();
    return this;
  }

  public jQuery ready() {
    jsref(".ready();");
    return this;
  }

  public jQuery refresh() {
    @SuppressWarnings("unchecked")
    List<Object> refresh = (List<Object>) js("return [" + ref + ".length," + ref + ".selector]");
    length = (Long) refresh.get(0);
    selector = (String) refresh.get(1);
    return this;
  }

  public Long refreshSize() {
    return length = (Long) jsret(".length");
  }

  public jQuery refreshUntil() throws TimeoutException {
    return refreshUntilAtLeast(1, jqf.getDefaultTimeout());
  }

  public jQuery refreshUntil(int min, int max, long timeout) throws TimeoutException {
    long to = System.currentTimeMillis() + timeout;
    while (true) {
      if (length() >= min) {
        if (max == -1 || length() <= max) {
          return this;
        }
      }

      if (System.currentTimeMillis() > to) {
        throw new TimeoutException("Looking for " + selector + " with " + min + " to " + max + " results.");
      }

      try {
        Thread.sleep(200);
        refresh();
      } catch (InterruptedException e) {
        throw new TimeoutException("Looking for " + selector + " with " + min + " to " + max + " results.");
      }
    }
  }

  public jQuery refreshUntil(long timeout) throws TimeoutException {
    return refreshUntilAtLeast(1, timeout);
  }

  public jQuery refreshUntilAtLeast(int min) throws TimeoutException {
    return refreshUntilAtLeast(min, jqf.getDefaultTimeout());
  }

  public jQuery refreshUntilAtLeast(int min, long timeout) throws TimeoutException {
    return refreshUntil(min, -1, timeout);
  }

  public jQuery refreshUntilAtMost(int max) throws TimeoutException {
    return refreshUntil(0, max, jqf.getDefaultTimeout());
  }

  public jQuery refreshUntilAtMost(int max, long timeout) throws TimeoutException {
    return refreshUntil(0, max, timeout);
  }

  public jQuery refreshUntilNone(long timeout) throws TimeoutException {
    return refreshUntil(0, 0, timeout);
  }

  public jQuery remove() {
    jsref(".remove()");
    return this;
  }

  public jQuery remove(String selector) {
    jsref(".remove(arguments[0])", selector);
    return this;
  }

  public jQuery removeAttr(String key) {
    jsref(".removeAttr(arguments[0]);", key);
    return this;
  }

  public jQuery removeClass(String cls) {
    jsref(".removeClass(arguments[0]);", cls);
    return this;
  }

  public jQuery removeData(String key) {
    jsref(".removeData(arguments[0]", key);
    return this;
  }

  public jQuery replaceAll(String selector) {
    return subset(".replaceAll(arguments[0])", selector);
  }

  public jQuery replaceWith(jQuery jq) {
    return subset(".replaceWith(" + jq.ref + ")");
  }

  public jQuery replaceWith(String selector_or_html) {
    return subset(".replaceWith(arguments[0])", selector_or_html);
  }

  public jQuery replaceWith(WebElement we) {
    return subset(".replaceWith(arguments[0])", we);
  }

  public jQuery resize() {
    jsref(".resize();");
    return this;
  }

  public jQuery scroll() {
    jsref(".scroll();");
    return this;
  }

  public Long scrollLeft() {
    return (Long) jsret(".scrollLeft()");
  }

  public jQuery scrollLeft(int left) {
    jsref(".scrollLeft(arguments[0])", left);
    return this;
  }

  public Long scrollTop() {
    return (Long) jsret(".scrollTop()");
  }

  public jQuery scrollTop(int top) {
    jsref(".scrollTop(arguments[0])", top);
    return this;
  }

  public jQuery select() {
    jsref(".select();");
    return this;
  }

  public jQuery selected() {
    return find(" :selected");
  }

  public String serialize() {
    return (String) jsret(".serialize()");
  }

  public jQuery setTimeout() {
    return setTimeout(jqf.getDefaultTimeout());
  }

  public jQuery setTimeout(long timeout) {
    this.timeout = timeout;
    this.startTime = System.currentTimeMillis();
    return this;
  }

  public jQuery show() {
    jsref(".show()");
    return this;
  }

  public jQuery siblings() {
    return subset(".siblings()");
  }

  public jQuery siblings(String selector) {
    return subset(".siblings(arguments[0])", selector);
  }

  public Long size() {
    return length;
  }

  public jQuery slice(int start) {
    return subset(".slice(arguments[0])", start);
  }

  public jQuery slice(int start, int end) {
    return subset(".slice(arguments[0],arguments[1])", start, end);
  }

  public jQuery slideDown() {
    jsref(".slideDown()");
    return this;
  }

  public jQuery slideToggle() {
    jsref(".slideToggle()");
    return this;
  }

  public jQuery slideUp() {
    jsref(".slideUp()");
    return this;
  }

  public jQuery sort() {
    jsref(".sort()");
    return this;
  }

  public jQuery stop() {
    jsref(".stop()");
    return this;
  }

  public jQuery submit() {
    jsref(".submit();");
    return this;
  }

  protected jQuery subset(String method_script, Object... args) {
    jQuery subset = new jQuery(jqf, createRef(), null, this);
    js(subset.ref + "=" + ref + method_script, args);
    subset.refresh();
    children.add(subset);
    return subset;
  }

  public String text() {
    return (String) jsret(".text();");
  }

  public jQuery text(String text) {
    jsref(".text(arguments[0]);", text);
    return this;
  }

  @SuppressWarnings("unchecked")
  public List<WebElement> toArray() {
    return (List<WebElement>) jsret(".toArray()");
  }

  public jQuery toggle() {
    jsref(".toggle();");
    return this;
  }

  public jQuery toggle(boolean toggle) {
    jsref(".toggle(arguments[0]);", toggle);
    return this;
  }

  public jQuery toggle(long duration) {
    jsref(".toggle(arguments[0]);", duration);
    return this;
  }

  public jQuery toggle(long duration, String easing) {
    jsref(".toggle(arguments[0],arguments[1]);", duration, easing);
    return this;
  }

  public jQuery toggleClass(String cls) {
    jsref(".toggleClass(arguments[0]);", cls);
    return this;
  }

  @Override
  public String toString() {
    if (selector != null) {
      return "$(" + selector + ")[" + length + "]:" + ref;
    } else {
      return "$(object)[" + length + "]:" + ref;
    }
  }

  public jQuery trigger(String event) {
    jsref(".trigger(arguments[0]);", event);
    return this;
  }

  public jQuery triggerHandler(String event) {
    jsref(".triggerHandler(arguments[0]);", event);
    return this;
  }

  public jQuery unbind(String event) {
    jsref(".unbind(arguments[0]);", event);
    return this;
  }

  public jQuery unload() {
    jsref(".unload();");
    return this;
  }

  public jQuery until(int min) throws TimeoutException {
    return until(min, -1, jqf.getDefaultTimeout());
  }

  public jQuery until(int min, int max, long timeout) throws TimeoutException {
    setTimeout(timeout);
    while (true) {
      init();
      if (length >= min) {
        if (max < 0 || length <= max) {
          clearTimeout();
          return this;
        }
      }

      if (isTimeout()) {
        clearTimeout();
        throw new TimeoutException();
      }

      pause();
    }
  }

  public jQuery until(int min, long timeout) throws TimeoutException {
    return until(min, -1, timeout);
  }

  public jQuery untilChanged() throws TimeoutException {
    return untilChanged(jqf.getDefaultTimeout());
  }

  public jQuery untilChanged(long timeout) throws TimeoutException {
    if (html_hash == -1) {
      throw new IllegalArgumentException("Cannot wait until changed if html has never been marked (mark())");
    }
    setTimeout(timeout);
    try {
      while (true) {
        if (html_hash != combinedMethodHash("html")) {
          return this;
        }
        init();
        pause("Waiting for " + toString() + " to change.");
      }
    } finally {
      html_hash = -1;
    }
  }

  public int untilCombinedAtterHashChanged(int previous_hash, String attr, long timeout) throws TimeoutException {
    setTimeout(timeout);
    while (true) {
      int attr_hash = combinedAttrHash(attr);
      if (attr_hash != previous_hash) {
        return attr_hash;
      }

      if (isTimeout()) {
        throw new TimeoutException();
      }
      pause();
    }
  }

  public int untilCombinedAttrHashChanged(int previous_hash, String attr) throws TimeoutException {
    return untilCombinedAtterHashChanged(previous_hash, attr, jqf.getDefaultTimeout());
  }

  public int untilCombinedMethodHashChanged(int previous_hash, String method) throws TimeoutException {
    return untilCombinedMethodHashChanged(previous_hash, method, jqf.getDefaultTimeout());
  }

  public int untilCombinedMethodHashChanged(int previous_hash, String method, long timeout) throws TimeoutException {
    long to = System.currentTimeMillis() + timeout;
    while (true) {
      int method_hash = combinedMethodHash(method);
      if (method_hash != previous_hash) {
        return method_hash;
      }

      if (to < System.currentTimeMillis()) {
        throw new TimeoutException();
      }

      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {
        throw new TimeoutException();
      }
    }
  }

  public jQuery untilLessThan(int max) throws TimeoutException {
    return until(0, max, jqf.getDefaultTimeout());
  }

  public jQuery untilLessThan(int max, long timeout) throws TimeoutException {
    return until(0, max, timeout);
  }

  public jQuery untilNone() throws TimeoutException {
    return until(0, 0, jqf.getDefaultTimeout());
  }

  public jQuery untilNone(long timeout) throws TimeoutException {
    return until(0, 0, timeout);
  }

  public jQuery unwrap(jQuery jq) {
    jsref(".unwrap(" + jq.ref + ")");
    return this;
  }

  public jQuery unwrap(String selector_or_html) {
    jsref(".unwrap(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery unwrap(WebElement we) {
    jsref(".unwrap(arguments[0])", we);
    return this;
  }

  public String val() {
    return (String) jsret(".val();");
  }

  public jQuery val(String val) {
    jsref(".val(arguments[0]);", val);
    return this;
  }

  public Long width() {
    return (Long) jsret(".width()");
  }

  public jQuery width(int width) {
    jsref(".width(arguments[0])", width);
    return this;
  }

  public jQuery wrap(jQuery jq) {
    jsref(".wrap(" + jq.ref + ")");
    return this;
  }

  public jQuery wrap(String selector_or_html) {
    jsref(".wrap(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery wrap(WebElement we) {
    jsref(".wrap(arguments[0])", we);
    return this;
  }

  public jQuery wrapAll(jQuery jq) {
    jsref(".wrapAll(" + jq.ref + ")");
    return this;
  }

  public jQuery wrapAll(String selector_or_html) {
    jsref(".wrapAll(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery wrapAll(WebElement we) {
    jsref(".wrapAll(arguments[0])", we);
    return this;
  }

  public jQuery wrapInner(jQuery jq) {
    jsref(".wrapInner(" + jq.ref + ")");
    return this;
  }

  public jQuery wrapInner(String selector_or_html) {
    jsref(".wrapInner(arguments[0])", selector_or_html);
    return this;
  }

  public jQuery wrapInner(WebElement we) {
    jsref(".wrapInner(arguments[0])", we);
    return this;
  }
}

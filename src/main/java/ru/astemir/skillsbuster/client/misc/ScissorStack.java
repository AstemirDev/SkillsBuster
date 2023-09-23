package ru.astemir.skillsbuster.client.misc;



import com.mojang.blaze3d.platform.Window;
import org.lwjgl.opengl.GL11;


import java.util.Stack;

public final class ScissorStack {
	private static final Stack<ClipInfo> STACK = new Stack<>();

	private record ClipInfo(int x, int y, int w, int h) {

		private ClipInfo(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = Math.max(0, w);
			this.h = Math.max(0, h);
		}

		public ClipInfo crop(int sx, int sy, int sw, int sh) {
			var x0 = Math.max(x, sx);
			var y0 = Math.max(y, sy);
			var x1 = Math.min(x + w, sx + sw);
			var y1 = Math.min(y + h, sy + sh);
			return new ClipInfo(x0, y0, x1 - x0, y1 - y0);
		}

		public void scissor(Window screen) {
			var scale = screen.getGuiScale();
			var sx = (int) (x * scale);
			var sy = (int) ((screen.getGuiScaledHeight() - (y + h)) * scale);
			var sw = (int) (w * scale);
			var sh = (int) (h * scale);
			GL11.glScissor(sx, sy, sw, sh);
		}
	}

	public static void pushScissor(Window screen, int x, int y, int w, int h) {
		if (STACK.isEmpty()) {
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
		}
		var scissor = STACK.isEmpty() ? new ClipInfo(x, y, w, h) : STACK.lastElement().crop(x, y, w, h);
		STACK.push(scissor);
		scissor.scissor(screen);
	}

	public static void popScissor(Window screen) {
		STACK.pop();
		if (STACK.isEmpty()) {
			GL11.glDisable(GL11.GL_SCISSOR_TEST);
		} else {
			STACK.lastElement().scissor(screen);
		}
	}
}
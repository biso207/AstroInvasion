package sorgente.GameMods;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

public class QuadTree {
    private static final int MAX_OBJECTS = 10; // Numero massimo di oggetti per nodo
    private static final int MAX_LEVELS = 5;  // Profondità massima del QuadTree

    private int level;            // Livello attuale
    private Array<Rectangle> objects; // Oggetti contenuti in questo nodo
    private Rectangle bounds;     // Limiti del nodo
    private QuadTree[] nodes;     // Sotto-nodi

    public QuadTree(int level, Rectangle bounds) {
        this.level = level;
        this.objects = new Array<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }

    // Svuota il QuadTree
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    // Divide il nodo in quattro sotto-nodi
    private void split() {
        int subWidth = (int) bounds.width / 2;
        int subHeight = (int) bounds.height / 2;
        int x = (int) bounds.x;
        int y = (int) bounds.y;

        nodes[0] = new QuadTree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new QuadTree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new QuadTree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new QuadTree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    // Ottiene l'indice del sotto-nodo che potrebbe contenere l'oggetto
    private int getIndex(Rectangle rect) {
        int index = -1;
        double verticalMidpoint = bounds.x + bounds.width / 2;
        double horizontalMidpoint = bounds.y + bounds.height / 2;

        // Controlla se l'oggetto è completamente nel quadrante superiore o inferiore
        boolean topQuadrant = rect.y > horizontalMidpoint;
        boolean bottomQuadrant = rect.y + rect.height <= horizontalMidpoint;

        // Controlla se l'oggetto è completamente nel quadrante sinistro o destro
        if (rect.x + rect.width <= verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (rect.x >= verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    // Inserisce un oggetto nel QuadTree
    public void insert(Rectangle rect) {
        if (nodes[0] != null) {
            int index = getIndex(rect);

            if (index != -1) {
                nodes[index].insert(rect);
                return;
            }
        }

        objects.add(rect);

        // Divide il nodo se supera il numero massimo di oggetti
        if (objects.size > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            Iterator<Rectangle> iterator = objects.iterator();
            while (iterator.hasNext()) {
                Rectangle obj = iterator.next();
                int index = getIndex(obj);
                if (index != -1) {
                    nodes[index].insert(obj);
                    iterator.remove();
                }
            }
        }
    }

    // Restituisce una lista di potenziali collisioni
    public Array<Rectangle> retrieve(Array<Rectangle> returnObjects, Rectangle rect) {
        int index = getIndex(rect);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, rect);
        }

        returnObjects.addAll(objects);
        return returnObjects;
    }
}

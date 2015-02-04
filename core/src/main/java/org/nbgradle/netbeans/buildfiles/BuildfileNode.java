/*
 */
package org.nbgradle.netbeans.buildfiles;

import java.beans.IntrospectionException;
import java.util.Collections;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author radim
 */
public class BuildfileNode extends AbstractNode {

    public BuildfileNode() {
        super(new BuildfileChildren(), Lookup.EMPTY);
    }

    private static class BuildfileChildren extends Children.Keys {

        public BuildfileChildren() {
            // setKeys(Collections.singleton(new Object()));
            setKeys(Collections.emptyList());
        }

        @Override
        protected Node[] createNodes(Object key) {
            try {
                return new Node[] {new BeanNode<>(key)};
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
                return new Node[0];
            }
        }
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ihtsdo.taxonomy.items;

import gov.va.demo.taxonomy.Icons;
import gov.va.demo.terminology.TerminologyService;
import java.io.IOException;
import java.util.*;
import javafx.scene.paint.Color;
import org.ihtsdo.helper.time.TimeHelper;
import org.ihtsdo.taxonomy.DescTypeForNodes;
import org.ihtsdo.tk.api.*;
import org.ihtsdo.tk.api.concept.ConceptChronicleBI;
import org.ihtsdo.tk.api.concept.ConceptVersionBI;
import org.ihtsdo.tk.api.coordinate.EditCoordinate;
import org.ihtsdo.tk.api.coordinate.ViewCoordinate;
import org.ihtsdo.tk.api.description.DescriptionVersionBI;
import org.ihtsdo.tk.api.media.MediaChronicleBI;
import org.ihtsdo.tk.api.media.MediaVersionBI;
import org.ihtsdo.tk.api.refex.RefexChronicleBI;
import org.ihtsdo.tk.api.refex.RefexVersionBI;
import org.ihtsdo.tk.api.refex.type_boolean.RefexBooleanVersionBI;
import org.ihtsdo.tk.api.refex.type_int.RefexIntVersionBI;
import org.ihtsdo.tk.api.refex.type_nid.RefexNidVersionBI;
import org.ihtsdo.tk.api.refex.type_string.RefexStringVersionBI;
import org.ihtsdo.tk.binding.RefexAux;
import org.openide.util.Exceptions;

/**
 *
 * @author kec
 */
public class TaxonomyItemSetup {

    private NidSet viewerImageTypes = new NidSet();
    private DescTypeForNodes typeToRender = DescTypeForNodes.FSN;
    private boolean showViewerImagesInTaxonomy = true;
    private boolean showRefsetInfoInTaxonomy = true;
    private boolean showPathInfoInTaxonomy = true;
    private NidList refsetsToShow = new NidList();
    private Map<Integer, Color> colorForPath;
    private EditCoordinate ec;
    private ViewCoordinate vc;
    private int viewerImageTypeNid;

    //~--- constructors --------------------------------------------------------
    public TaxonomyItemSetup(ViewCoordinate vc, EditCoordinate ec, Map<Integer, Color> colorForPath)
            throws IOException {
        super();
        this.vc = vc;
        this.ec = ec;
        this.colorForPath = colorForPath;

        ConceptChronicleBI viewerImageType = TerminologyService.getStore().getConcept(RefexAux.VIEWER_IMAGE.getUuids());

        viewerImageTypes.add(viewerImageType.getConceptNid());
        viewerImageTypeNid = viewerImageType.getConceptNid();
    }

    //~--- methods -------------------------------------------------------------
    public void setupTaxonomyNode(Item node, ConceptVersionBI cv) throws IOException {
        List<String> htmlPrefixes = new ArrayList<String>();
        List<String> htmlSuffixes = new ArrayList<String>();
        boolean defined = false;

        try {
            if (cv.getConAttrsActive() != null) {
                defined = cv.getConAttrsActive().isDefined();
            }
        } catch (ContradictionException ex) {
            defined = cv.getConAttrs().getVersions(cv.getViewCoordinate()).iterator().next().isDefined();
        }

        Set<Color> colors = new HashSet<Color>();

        for (int sapNid : cv.getAllSapNids()) {
            colors.add(colorForPath.get(TerminologyService.getStore().getPathNidForSapNid(sapNid)));
        }

        List<Color> pathColors = new ArrayList<Color>(colors);

        node.setPathColors(pathColors);

        if (showViewerImagesInTaxonomy) {
            try {
                for (MediaVersionBI media : cv.getMediaActive()) {
                    if (media.getTypeNid() == viewerImageTypeNid) {
                        htmlPrefixes.add("<img src='ace:" + media.getNid() + "$" + media.getConceptNid()
                                + "' align=center>");
                    }
                }
            } catch (ContradictionException ex) {
                htmlPrefixes.add("media in conflict");
            }
        }

        if (showPathInfoInTaxonomy) {
            showPathInfoInTaxonomy(cv, node, htmlSuffixes);
        }

        if (showRefsetInfoInTaxonomy) {
            showRefsetInfoInTaxonomy(cv, htmlPrefixes, htmlSuffixes);
        }

        StringBuilder buff = new StringBuilder();
        String conceptDesc;

        switch (typeToRender) {
            case FSN:
                try {
                    DescriptionVersionBI desc = cv.getFullySpecifiedDescription();

                    if (desc != null) {
                        conceptDesc = desc.getText();
                        node.setSortComparable(conceptDesc.toLowerCase());
                    } else {
                        conceptDesc = "no fsn";
                        node.setSortComparable(conceptDesc.toLowerCase());
                    }
                } catch (ContradictionException ex) {
                    conceptDesc = cv.getFsnDescsActive().iterator().next().getText();
                    node.setSortComparable(conceptDesc.toLowerCase());
                }

                break;

            case PREFERRED:
                try {
                    DescriptionVersionBI desc = cv.getPreferredDescription();

                    if (desc != null) {
                        conceptDesc = desc.getText();
                        node.setSortComparable(conceptDesc.toLowerCase());
                    } else {
                        conceptDesc = "no fsn";
                        node.setSortComparable(conceptDesc.toLowerCase());
                    }

                    node.setSortComparable(conceptDesc.toLowerCase());
                } catch (ContradictionException ex) {
                    conceptDesc = cv.getPrefDescsActive().iterator().next().getText();
                    node.setSortComparable(conceptDesc.toLowerCase());
                }

                break;

            default:
                throw new UnsupportedOperationException("Can't handle: " + typeToRender);
        }

        if (conceptDesc.toLowerCase().startsWith("<html>")) {
            conceptDesc = conceptDesc.substring(5);
        }

        if ((htmlPrefixes.size() > 0) || (htmlSuffixes.size() > 0)) {
            buff.append("<html>");

            for (String prefix : htmlPrefixes) {
                buff.append(prefix);
            }

            String text = conceptDesc;

            if (text.toLowerCase().startsWith("<html>")) {
                buff.append(text.substring(5));
            } else {
                buff.append(text);
            }

            for (String suffix : htmlSuffixes) {
                buff.append(suffix);
            }
        } else {
            buff.append(conceptDesc);
        }

        node.setText(buff.toString());

        if (cv.getRelsOutgoingDestinationsNidsActiveIsa().length == 0) {
            node.setIcon(Icons.ROOT);
        } else {
            if (defined) {
                node.setIcon(Icons.DEFINED_SINGLE_PARENT);
            } else {
                node.setIcon(Icons.PRIMITIVE_SINGLE_PARENT);
            }

            if (node.hasExtraParents() && !node.isSecondaryParentNode()) {
                if (node.isSecondaryParentOpened()) {
                    if (defined) {
                        node.setIcon(Icons.DEFINED_MULTI_PARENT_OPEN);
                    } else {
                        node.setIcon(Icons.PRIMITIVE_MULTI_PARENT_OPEN);
                    }
                } else {
                    if (defined) {
                        node.setIcon(Icons.DEFINED_MULTI_PARENT_CLOSED);
                    } else {
                        node.setIcon(Icons.PRIMITIVE_MULTI_PARENT_CLOSED);
                    }
                }
            } else {
                if (node.isSecondaryParentNode()) {
                    if (node.isSecondaryParentOpened()) {
                        if (defined) {
                            node.setIcon(Icons.DEFINED_MULTI_PARENT_OPEN);
                        } else {
                            node.setIcon(Icons.PRIMITIVE_MULTI_PARENT_OPEN);
                        }
                    } else {
                        if (defined) {
                            node.setIcon(Icons.DEFINED_MULTI_PARENT_CLOSED);
                        } else {
                            node.setIcon(Icons.PRIMITIVE_MULTI_PARENT_CLOSED);
                        }
                    }

                }

            }
        }
    }

    private void showPathInfoInTaxonomy(ConceptVersionBI cv, Item node, List<String> htmlSuffixes)
            throws IOException {
        if (TerminologyService.getStore().hasPath(cv.getConceptNid())) {
            node.setToolTipText(getChildrenToolTipText(cv));

            PositionBI latestInheritedViewPosition = null;

            for (int editPathNid : ec.getEditPaths().getSetValues()) {
                if (editPathNid == cv.getConceptNid()) {
                    htmlSuffixes.add("<font color=red>&nbsp;[Editing]</font>");
                }
            }

            for (PositionBI viewPosition : vc.getPositionSet()) {
                if (viewPosition.getPath().getConceptNid() == cv.getConceptNid()) {
                    String version = TimeHelper.formatDate(viewPosition.getTime());

                    htmlSuffixes.add("<font color='#007FAE'>&nbsp;[Viewing:" + version + "]</font>");
                }

                for (PositionBI origin : viewPosition.getPath().getNormalisedOrigins()) {
                    if (origin.getPath().getConceptNid() == cv.getConceptNid()) {
                        if ((latestInheritedViewPosition == null)
                                || (origin.getTime() > latestInheritedViewPosition.getTime())) {
                            latestInheritedViewPosition = origin;
                        }
                    }
                }
            }

            if (latestInheritedViewPosition != null) {
                String version = TimeHelper.formatDate(latestInheritedViewPosition.getTime());

                htmlSuffixes.add("<font color='#967F49'>&nbsp;[Inherited view:" + version + "]</font>");
            }
        }
    }

    private void showRefsetInfoInTaxonomy(ConceptVersionBI cv, List<String> htmlPrefixes,
            List<String> htmlSuffixes)
            throws IOException {
        Collection<RefexChronicleBI> extensions = new ArrayList<RefexChronicleBI>();

        extensions.addAll(cv.getRefexes());

        HashSet<Integer> refexAlreadyHandled = new HashSet<Integer>();

        for (int i : refsetsToShow.getListArray()) {
            for (RefexChronicleBI ebr : extensions) {
                if (!refexAlreadyHandled.contains(i)) {
                    if ((ebr != null) && (ebr.getRefexNid() == i)) {
                        if (ebr instanceof RefexBooleanVersionBI) {
                            for (RefexVersionBI t : ebr.getCurrentRefexes(cv.getViewCoordinate())) {
                                boolean extValue = ((RefexBooleanVersionBI) t).getBoolean1();

                                refexAlreadyHandled.add(i);

                                try {
                                    ConceptVersionBI booleanImageBean =
                                            TerminologyService.getSnapshot().getConceptVersion(
                                            RefexAux.BOOLEAN_CIRCLE_ICONS_FALSE.getUuids());

                                    if (extValue) {
                                        TerminologyService.getSnapshot().getConceptVersion(
                                                RefexAux.BOOLEAN_CIRCLE_ICONS_TRUE.getUuids());
                                    }

                                    for (MediaVersionBI imageTuple : booleanImageBean.getMediaActive()) {
                                        if (viewerImageTypes.contains(imageTuple.getTypeNid())) {
                                            htmlPrefixes.add("<img src='ace:" + imageTuple.getNid() + "$"
                                                    + imageTuple.getConceptNid() + "' align=center>");
                                        }
                                    }
                                } catch (ContradictionException e) {
                                    Exceptions.printStackTrace(e);
                                } catch (IOException e) {
                                    Exceptions.printStackTrace(e);
                                }
                            }
                        } else if (ebr instanceof RefexNidVersionBI) {
                            RefexNidVersionBI extVersion = (RefexNidVersionBI) ebr;
                            ConceptVersionBI ebrCb = TerminologyService.getStore().getConceptVersion(cv.getViewCoordinate(),
                                    extVersion.getNid1());

                            refexAlreadyHandled.add(i);

                            try {
                                for (MediaChronicleBI imageTuple : ebrCb.getMediaActive()) {
                                    htmlPrefixes.add("<img src='ace:" + imageTuple.getNid() + "$"
                                            + imageTuple.getConceptNid() + "' align=center>");
                                }
                            } catch (ContradictionException ex) {
                                for (MediaChronicleBI imageTuple : ebrCb.getMedia()) {
                                    htmlPrefixes.add("<img src='ace:" + imageTuple.getNid() + "$"
                                            + imageTuple.getConceptNid() + "' align=center>");
                                }
                            }
                        } else if (ebr instanceof RefexIntVersionBI) {
                            int extValue = ((RefexIntVersionBI) ebr).getInt1();

                            htmlPrefixes.add("<font color=blue>&nbsp;" + extValue + "&nbsp;</font>");
                        } else if (ebr instanceof RefexStringVersionBI) {
                            String strExt = ((RefexStringVersionBI) ebr).getString1();

                            refexAlreadyHandled.add(i);
                            htmlSuffixes.add("<code><strong>" + strExt + "'</strong></code>");
                        }
                    }
                }
            }
        }
    }

    //~--- get methods ---------------------------------------------------------
    private String getChildrenToolTipText(ConceptVersionBI cv) throws IOException {
        StringBuilder toolTipText = new StringBuilder();

        toolTipText.append("<html>");

        int originCount = 0;

        for (PositionBI child : TerminologyService.getStore().getPath(cv.getConceptNid()).getOrigins()) {
            originCount++;
            toolTipText.append("<font color=blue>origin:</font> ");
            toolTipText.append(child.toString());
            toolTipText.append("<br>");
        }

        if (originCount == 0) {
            toolTipText.append("no origins<br><br>");
        } else {
            toolTipText.append("<br>");
        }

        int childCount = 0;

        for (PathBI child : TerminologyService.getStore().getPathChildren(cv.getConceptNid())) {
            childCount++;
            toolTipText.append("<font color=green>child:</font> ");
            toolTipText.append(child.toString());
            toolTipText.append("<br>");
        }

        if (childCount == 0) {
            toolTipText.append("no children");
        }

        return toolTipText.toString();
    }

    public EditCoordinate getEditCoordinate() {
        return ec;
    }

    public String getOrder(ConceptVersionBI cv) throws IOException {
        switch (typeToRender) {
            case FSN:
                try {
                    return cv.getFullySpecifiedDescription().getText() + '\u039A';
                } catch (ContradictionException ex) {
                    return cv.getFsnDescsActive().iterator().next().getText() + '\u039A';
                }
            case PREFERRED:
                try {
                    return cv.getPreferredDescription().getText() + '\u039A';
                } catch (ContradictionException ex) {
                    return cv.getPrefDescsActive().iterator().next().getText() + '\u039A';
                }
            default:
                throw new UnsupportedOperationException("Can't handle: " + typeToRender);
        }
    }

    public ViewCoordinate getViewCoordinate() {
        return vc;
    }

    //~--- set methods ---------------------------------------------------------
    protected void setTypeToRender(DescTypeForNodes typeToRender) {
        this.typeToRender = typeToRender;
    }
}

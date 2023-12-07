import React, { useMemo } from "react";
import NavigateNextIcon from '@mui/icons-material/NavigateNext';
import { Breadcrumbs, Link, Typography } from "@mui/material";
import { useFillActions, useFillSession } from "@dialob/fill-react";

export interface BreadCrumbsProps {
  items: string[];
  activeItem?: string;
  canNavigate: boolean;
}

export const BreadCrumbs: React.FC<BreadCrumbsProps> = ({ items, activeItem, canNavigate }) => {
  const session = useFillSession();
  const actions = useFillActions();

  const needsBreadCrumb = useMemo(() => {
    if (items.length === 1) {
      // No point for breadcrumbs if there's only one page available
      return false;
    }

    for (let id of items) {
      // check if we don't have any accessible page information available within fill session
      // if not, we can't get page labels for breadcrumbs. This means that "show only active questions" option was set for the form, which
      // turns off the breadcrumbs
      if (!session.getItem(id)) {
        return false;
      }
    }

    // Otherwise we need breadcrumbs
    return true;
  }, [items]);

  if (!needsBreadCrumb) {
    return null;
  }

  return (
    <Breadcrumbs separator={<NavigateNextIcon fontSize="small" />} >
      {
        items.map((item, k) => {
          if (activeItem === item) {
            return <Typography key={k} color="text.primary"><strong>{session.getItem(item)?.label}</strong></Typography> ;   
          } else {
            return <Link key={k} component="button" disabled={!canNavigate} underline={canNavigate ? "hover" : "none"} color={canNavigate ? "primary" : "inherit"} onClick={() => actions.goToPage(item)}>{session.getItem(item)?.label}</Link>;
          }
        })
      }
    </Breadcrumbs>
  );
}

import { Box, Button, Typography } from "@mui/material";
import { useComposer } from "../dialob";
import { buildTreeFromForm } from "./tree/TreeBuilder";
import { TreeItem } from "@atlaskit/tree";


const DebugFormView: React.FC = () => {
  const { form, addItem } = useComposer();
  const tree = Object.values(buildTreeFromForm(form.data).items).filter(item => item.id != 'root');

  function onNewItemClick() {
    addItem({ id: '', type: 'text', view: 'debug', label: { en: 'Test' } }, 'group15');
  }

  const calculateDepth = (node: TreeItem, parentDepth: number = 0): number => {
    if (node.children.length === 0) {
      return parentDepth;
    }

    const childDepths = node.children.map((childId) => {
      const childNode = tree.find((item) => item.id === childId);
      return childNode ? calculateDepth(childNode, parentDepth + 1) : parentDepth + 1;
    });

    return Math.max(...childDepths);
  };

  return (
    <Box sx={{ ml: 10 }}>
      <Box>
        <Button onClick={onNewItemClick}>Add</Button>
      </Box>
      <Box>
        {
          tree.map(item => {
            const depth = calculateDepth(item);
            const childSx = { ml: depth * -2 };
            return (
              <Typography sx={childSx} key={item.id}>{item.id}</Typography>
            );
          })
        }
      </Box>
    </Box>
  );
}

export default DebugFormView;

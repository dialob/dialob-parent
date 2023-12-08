import { Box, Button, Typography } from "@mui/material";
import { useComposer } from "../dialob";

// Debug view listing all item ID-s in the given form state
export const DebugFormView: React.FC = () => {
 const { form, addItem } = useComposer();

 function onNewItemClick() {
    addItem({id: '', type: 'text', view: 'debug', label: { en: 'Test'}}, 'group15');
 }

 return (
  <>
  <Box>
    <Button onClick={onNewItemClick}>Add</Button>
  </Box>
   <Box>
      {
        Object.keys(form.data).map(itemId => <Typography key={itemId}>{itemId}</Typography>)
      }
   </Box> 
   </>
 );
}
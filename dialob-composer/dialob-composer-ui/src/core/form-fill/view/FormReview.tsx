import React from 'react';
import { CircularProgress, ThemeProvider } from '@mui/material';
import { MaterialDialobReview } from '@resys/dialob-review-material';


const FormReview: React.FC<{}> = ({ }) => {

  const [form, setForm] = React.useState<any>(null);

  if (!form) {
    return (<CircularProgress />)
  }

  const handleClose = () => {
    
  }

  return (
    <>
      <MaterialDialobReview formData={form.form} sessionData={form.session} />
    </>
  );
}


export default FormReview;
import { Grid, makeStyles, TextField } from "@material-ui/core";
import { Autocomplete } from "@material-ui/lab";
import { ItemAction, SessionError } from "@resys/dialob-fill-api";
import { useFillActions, useFillLocale } from "@resys/dialob-fill-react";
import React, { useCallback, useContext, useRef, useState } from "react";
import { fetchMapbox, MapboxContext, MapboxFeature, useMapbox } from '@resys/mapbox-connector';
import LocationOnIcon from '@material-ui/icons/LocationOn';
import { DescriptionWrapper, renderErrors } from "..";

const useStyles = makeStyles((theme) => ({
  icon: {
    color: theme.palette.text.secondary,
    marginRight: theme.spacing(2)
  }
}));

export interface AddressProps {
  address: ItemAction<any, any, string>['item'];
  errors: SessionError[];
};

export const Address: React.FC<AddressProps> = ({ address, errors }) => {
  const { setAnswer } = useFillActions();
  const mapboxContext = useContext(MapboxContext);
  const locale = useFillLocale();
  const classes = useStyles();
  const [inputValue, setInputValue] = useState('');

  const result = useMapbox(inputValue || address.value, {
    country: address.props?.country,
    language: locale,
    autocomplete: true,
    types: ['address'],
    limit: 5
  });

  const options = useRef(result?.features);
  options.current = result?.features;

  const acceptSuggestion = useCallback(async (feature: MapboxFeature) => {
    if (feature === null) {
      // Handle empty case
      setAnswer(address.id, null);
      return;
    }
    const result = await fetchMapbox(mapboxContext.token, feature.place_name, {
      endpoint: 'places-permanent',
      country: address.props?.country,
      language: locale,
      types: ['address'],
      limit: 1
    });
    if (!result.features[0]) return;
    setAnswer(address.id, result.features[0].place_name);
  }, [address.id, address.props, setAnswer, locale, mapboxContext.token]); 

  const value = options.current?.find(opt => opt.place_name === address.value) || {place_name: inputValue} as MapboxFeature;

  return (
    <DescriptionWrapper text={address.description} title={address.label}>
      <Autocomplete

        autoComplete
        filterSelectedOptions
        includeInputInList

        filterOptions={(x) => x}
        options={options.current || []}

        value={value}

        onChange={(event: any, newValue: any) => { 
          acceptSuggestion(newValue);
        }}

        onInputChange={(event: any, newInputValue: string) => {
          setInputValue(newInputValue);
        }}

        getOptionSelected={(option, value) => {
          const equal = option.id === value.id;
          return equal;
        }
        }

        getOptionLabel={(option) => {
            if (typeof option === 'string') {
              return option
             }  else { 
               return option.place_name || '';
             }
        }}

        renderInput={(params) => (
          <TextField {...params} inputProps={{ ...params.inputProps, autoComplete: 'new-password' }} label={address.label} error={errors.length > 0} helperText={renderErrors(errors)} />
        )}

        renderOption={(option) => {
          return (
            <Grid container alignItems='center'>
              <Grid item>
                <LocationOnIcon className={classes.icon} />
              </Grid>
              <Grid item xs>
                {option.place_name}
              </Grid>
            </Grid>
          );
        }}
      />
    </DescriptionWrapper>
  );

}

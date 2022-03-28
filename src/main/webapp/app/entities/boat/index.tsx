import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Boat from './boat';
import BoatDetail from './boat-detail';
import BoatUpdate from './boat-update';
import BoatDeleteDialog from './boat-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={BoatUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={BoatUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={BoatDetail} />
      <ErrorBoundaryRoute path={match.url} component={Boat} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={BoatDeleteDialog} />
  </>
);

export default Routes;

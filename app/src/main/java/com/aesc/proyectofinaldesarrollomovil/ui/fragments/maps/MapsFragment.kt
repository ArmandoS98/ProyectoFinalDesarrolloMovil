package com.aesc.proyectofinaldesarrollomovil.ui.fragments.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aesc.proyectofinaldesarrollomovil.R
import com.aesc.proyectofinaldesarrollomovil.extension.toast
import com.aesc.proyectofinaldesarrollomovil.provider.firebase.daos.LocationDao
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogError
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogGetCurrentLocation
import com.aesc.proyectofinaldesarrollomovil.utils.Utils.dialogInfo
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MapsFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener {

    companion object {
        const val LOCATION_REQUEST_CODE = 0
    }

    private lateinit var viewModel: MapsViewModel
    private lateinit var map: GoogleMap
    private lateinit var locationDao: LocationDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        location()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.maps_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.maps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MapsViewModel::class.java]
        createMapFragment()
    }

    private fun createMapFragment() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(gMap: GoogleMap) {
        map = gMap
        createMarker()
        map.setOnMyLocationClickListener(this)
        map.setOnMyLocationButtonClickListener(this)
        enableMyLocation()
    }

    private fun createMarker() {
        val favoritePlace = LatLng(14.6415565, -90.5138935)
        map.addMarker(MarkerOptions().position(favoritePlace).title("Hello World!"))
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(favoritePlace, 18f),
            4000,
            null
        )
    }

    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableMyLocation() {
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            dialogInfo(requireContext(), getString(R.string.msg_aceptar_permisos))
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                dialogInfo(requireContext(), getString(R.string.msg_activar_localizacion))
            }
            else -> {
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!::map.isInitialized) return
        if (!isPermissionsGranted()) {
            map.isMyLocationEnabled = false
            dialogInfo(requireContext(), getString(R.string.msg_activar_localizacion))
        }
    }

    override fun onMyLocationClick(currentLocation: Location) {
        showBottomSheetDialog(currentLocation)
    }

    private fun showBottomSheetDialog(currentLocation: Location) {

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)

        val name = bottomSheetDialog.findViewById<TextInputEditText>(R.id.tieNameOfLocation)
        val cancel = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnCancel)
        val send = bottomSheetDialog.findViewById<MaterialButton>(R.id.btnSend)

        cancel!!.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        send!!.setOnClickListener {
            val names = name!!.text.toString()
            if (names.isNotEmpty()) {
                locationDao = LocationDao()
                currentLocation.let {
                    val nameLocation = name.text.toString()
                    locationDao.addLocation(
                        currentLocation.latitude,
                        currentLocation.longitude,
                        nameLocation
                    )
                }

                bottomSheetDialog.dismiss()
                requireActivity().toast("Informacion Almacenada")
            } else {
                dialogError(requireContext(), "Ingresa un nombre\npara la ubicacion")
            }

        }
        bottomSheetDialog.show()
    }

    private fun location() {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gpsEnabled && !networkEnabled) {
            dialogGetCurrentLocation(requireContext())
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        location()
        return false
    }
}